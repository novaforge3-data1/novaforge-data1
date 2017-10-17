/**
 * Copyright ( c ) 2011-2014, BULL SAS, NovaForge Version 3 and above.
 *
 * This file is free software: you may redistribute and/or 
 * modify it under the terms of the GNU Alffero General Public License
 * as published by the Free Software Foundation, version 3 of the License.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Alffero General Public License for more details.
 * You should have received a copy of the GNU Alffero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU AGPL version 3 (AGPL-3.0) section 7
 *
 * If you modify this Program, or any covered work,
 * by linking or combining it with libraries listed
 * in COPYRIGHT file at the top-level directof of this
 * distribution (or a modified version of that libraries),
 * containing parts covered by the terms of licenses cited
 * in the COPYRIGHT file, the licensors of this Program
 * grant you additional permission to convey the resulting work.
 */
package org.novaforge.forge.authentification.webdav;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.SessionUser;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.filter.beans.DependencyInjectedFilter;
import org.alfresco.repo.webdav.auth.BaseAuthenticationFilter;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * WebDAV Authentication Filter Class used for CAS
 * 
 * @author Guillaume Lamirand
 */
public class CasAuthenticationFilter extends BaseAuthenticationFilter implements
DependencyInjectedFilter
{
	// Debug logging

	private static Log logger = LogFactory.getLog(CasAuthenticationFilter.class);

	// Authenticated user session object name

	private static final String PPT_EXTN = ".ppt";

	/** The password encodings to try in priority order **/
	private static final String[] ENCODINGS = new String[] {
	"UTF-8", 
	System.getProperty("file.encoding"),
	"ISO-8859-1"
	};

	/** Corresponding array of CharsetDecoders with CodingErrorAction.REPORT. Duplicates removed. */
	private static final CharsetDecoder[] DECODERS;

	static
	{
	Map<String, CharsetDecoder> decoders = new LinkedHashMap<String, CharsetDecoder>(ENCODINGS.length * 2);
	for (String encoding : ENCODINGS)
	{
	    if (!decoders.containsKey(encoding))
	    {
		decoders.put(encoding, Charset.forName(encoding).newDecoder()
		        .onMalformedInput(CodingErrorAction.REPORT));
	    }
	}
	DECODERS = new CharsetDecoder[decoders.size()];
	decoders.values().toArray(DECODERS);
	}

	/**
	* Run the authentication filter
	* 
	* @param context ServletContext
	* @param req ServletRequest
	* @param resp ServletResponse
	* @param chain FilterChain
	* @exception ServletException
	* @exception IOException
	*/
	public void doFilter(ServletContext context, ServletRequest req, ServletResponse resp, FilterChain chain)
	    throws IOException, ServletException
	{
		// Debug
		if (logger.isDebugEnabled()) {
		    logger.debug("Webdav request intercepted.");
		}
		// Assume it's an HTTP request
		final HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;

		// Get the user details object from the session
		SessionUser user = getSessionUser(context, httpReq, httpResp, true);

		if (user == null)
		{

		    // Debug
		    if (logger.isDebugEnabled()) {
			   logger.debug("Try to authenticate the user with Authorization header.");
		    }
		    // Get the authorization header
		    
		    String authHdr = httpReq.getHeader("Authorization");
		    
		    if ( authHdr != null && authHdr.length() > 5 && authHdr.substring(0,5).equalsIgnoreCase("BASIC"))
		    {
			// Basic authentication details present
			byte[] encodedString = Base64.decodeBase64(authHdr.substring(5).getBytes());
		
			// ALF-13621: Due to browser inconsistencies we have to try a fallback path of encodings
			Set<String> attemptedAuths = new HashSet<String>(DECODERS.length * 2);
			for (CharsetDecoder decoder : DECODERS)
			{                  
			    try
			    {
				// Attempt to decode using this charset 
				String basicAuth = decoder.decode(ByteBuffer.wrap(encodedString)).toString();
				
				// It decoded OK but we may already have tried this string.
				if (!attemptedAuths.add(basicAuth))
				{
				    // Already tried - no need to try again
				    continue;
				}
				String username;
				String password;

				// Split the username and password
				int pos = basicAuth.indexOf(":");
				if (pos != -1)
				{
				    username = basicAuth.substring(0, pos);
				    password = basicAuth.substring(pos + 1);
				}
				else
				{
				    username = basicAuth;
				    password = "";
				}
				// Authenticate the user using CAS RESTfull API
			      	final String casServer = System.getProperty("alfresco.cas.rest.server",
				    "https://localhost:8443/cas/v1/tickets");
				final String ticketGrantingTicket = getTicketGrantingTicket(casServer, username, password);
				if (logger.isDebugEnabled()){
					logger.debug(String.format("Retrieve Cas Granting ticket [TGT=%s]",ticketGrantingTicket));
				}
	 			String service = httpReq.getRequestURL().toString();
				final String serviceTicket = getServiceTicket(casServer, ticketGrantingTicket, service);
				if (logger.isDebugEnabled()){
					logger.debug(String.format("Validated Cas Granting ticket and retrieve Cas Service ticket [ST=%s]",serviceTicket));
				}
				if (serviceTicket == null) {
					break;
				}
			
				final String userName  = username;
				user = transactionService.getRetryingTransactionHelper().doInTransaction(
				new RetryingTransactionHelper.RetryingTransactionCallback<SessionUser>()
				{

				    public SessionUser execute() throws Throwable
				    {
				        try
				        {
				            // Authenticate the user
				            authenticationComponent.clearCurrentSecurityContext();
				            authenticationComponent.setCurrentUser(userName);

				            return createUserEnvironment(httpReq.getSession(), userName,
				                    authenticationService.getCurrentTicket(), true);
				        } catch (AuthenticationException ex)
				        {
				            if (logger.isDebugEnabled())
				            {
				                logger.debug("Failed", ex);
				            }
				            return null;
				        }
				    }
				});
				
				// Success so break out
				break;
			    }
			    catch (CharacterCodingException e)
			    {
				// Didn't decode using this charset. Try the next one or fail
			    }
			    catch (AuthenticationException ex)
			    {
				// Do nothing, user object will be null
			    }
			    catch (NoSuchPersonException e)
			    {
				// Do nothing, user object will be null
			    }
			}		    
		    }
		    else
		    {
			// Check if the request includes an authentication ticket

			String ticket = req.getParameter(ARG_TICKET);

			if (ticket != null && ticket.length() > 0)
			{
			    // PowerPoint bug fix
			    if (ticket.endsWith(PPT_EXTN))
			    {
				ticket = ticket.substring(0, ticket.length() - PPT_EXTN.length());
			    }

			    // Debug
			    if (logger.isDebugEnabled())
				logger.debug("Logon via ticket from " + req.getRemoteHost() + " (" + req.getRemoteAddr() + ":"
				        + req.getRemotePort() + ")" + " ticket=" + ticket);

			    // Validate the ticket
			    authenticationService.validate(ticket);

			    // Need to create the User instance if not already available
			    String currentUsername = authenticationService.getCurrentUserName();

			    user = createUserEnvironment(httpReq.getSession(), currentUsername, ticket, true);
			}
		    }

		    // Check if the user is authenticated, if not then prompt again
		    
		    if ( user == null)
		    {
		 	// Debug
			if (logger.isDebugEnabled()) {
			   logger.debug("No user/ticket, force the client to prompt for logon details.");
			}
			// No user/ticket, force the client to prompt for logon details

			httpResp.setHeader("WWW-Authenticate", "BASIC realm=\"NovaForge :: Alfresco DAV Server\"");
			httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			httpResp.flushBuffer();
			return;
		    }
		}

		// Chain other filters
		chain.doFilter(req, resp);
	}

	private static String getServiceTicket(final String pServer, final String pTicketGrantingTicket,
			final String pService)
	{
		if (pTicketGrantingTicket == null)
		{
			return null;
		}

		final HttpClient client = new HttpClient();
		final PostMethod post = new PostMethod(pServer + "/" + pTicketGrantingTicket);
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service", pService) });

		try
		{
			client.executeMethod(post);
			final String response = post.getResponseBodyAsString();
			switch (post.getStatusCode())
			{
				case 200:
					return response;

				default:
					logger.warn("Invalid response code (" + post.getStatusCode() + ") from CAS server!");
					break;
			}
		}

		catch (final IOException e)
		{
			logger.warn(e.getMessage());
		}
		finally
		{
			post.releaseConnection();
		}

		return null;
	}

	private static String getTicketGrantingTicket(final String pServer, final String pUsername,
			final String pPassword)
	{
		final HttpClient client = new HttpClient();
		final PostMethod post = new PostMethod(pServer);
		post.setRequestBody(new NameValuePair[] { new NameValuePair("username", pUsername),
				new NameValuePair("password", pPassword) });

		try
		{
			client.executeMethod(post);
			final String response = post.getResponseBodyAsString();
			switch (post.getStatusCode())
			{
				case 201:
				{
					final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(response);
					if (matcher.matches())
					{
						return matcher.group(1);
					}
					logger.warn("Successful ticket granting request, but no ticket found!");
					break;
				}

				default:
					logger.warn("Invalid response code (" + post.getStatusCode() + ") from CAS server!");
					break;
			}
		}

		catch (final IOException e)
		{
			logger.warn(e.getMessage());
		}
		finally
		{
			post.releaseConnection();
		}

		return null;
	}

	private static void notNull(final Object object, final String message)
	{
		if (object == null)
		{
			throw new IllegalArgumentException(message);
		}
	}

	/**
	* Cleanup filter resources
	*/
	public void destroy()
	{
	// Nothing to do
	}

	/* (non-Javadoc)
	* @see org.alfresco.repo.webdav.auth.BaseAuthenticationFilter#getLogger()
	*/
	protected Log getLogger()
	{
	return logger;
	}
}
