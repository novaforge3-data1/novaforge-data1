/**
 * Copyright (c) 2011-2015, BULL SAS, NovaForge Version 3 and above.
 *
 * This file is free software: you may redistribute and/or 
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation, version 3 of the License.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
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
/**
 * 
 */
package org.polarion.svnwebclient.authorization.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.polarion.svnwebclient.SVNWebClientException;
import org.polarion.svnwebclient.authorization.UserCredentials;
import org.polarion.svnwebclient.web.SystemInitializing;
import org.polarion.svnwebclient.web.resource.Links;
import org.polarion.svnwebclient.web.support.RequestParameters;
import org.polarion.svnwebclient.web.support.State;

public class NovaforgeAuthorizationFilter implements Filter
{
  private static String[] EXCLUDES_PATTERN     = { "/restrictedAccess.jsp", "/script", "/style", "/images" };
  private static String[] WRITE_ACCESS_PATTERN = { "/directoryAddAction.jsp", "/fileAddAction.jsp",
      "/fileUpdateAction.jsp", "/deleteAction.jsp" };
  private String          connectionURL        = null;
  private String          dbUserName           = null;
  private String          dbPassword           = null;
  private String          dbClassName          = null;

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(final FilterConfig config) throws ServletException
  {
    Logger.getLogger(this.getClass()).debug("Init filter" + getClass());

    connectionURL = config.getInitParameter("db.url");
    dbUserName = config.getInitParameter("db.username");
    dbPassword = config.getInitParameter("db.password");
    dbClassName = config.getInitParameter("db.className");

  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy()
  {
    Logger.getLogger(this.getClass()).debug("Destroy filter" + getClass());

  }

  private boolean isAuthorized(final String pLogin, final String pLocation, final boolean pNeedWriteAccess)
      throws ServletException
  {

    boolean result = false;
    ResultSet rs = null;
    Statement statement = null;
    Connection connection = null;
    /*
     * select u.name, up.write_permission, rp.path, r.name from SVN_USER as
     * u, SVN_USER_PERMISSION up, SVN_REPOSITORY_PATH rp, SVN_REPOSITORY r
     * where u.id=up.user_id and rp.id=up.repository_path_id and
     * rp.repository_id=r.id and u.name='user1' and r.name='project1_svn1';
     */
    try
    {
      Class.forName(dbClassName).newInstance();
      connection = DriverManager.getConnection(connectionURL, dbUserName, dbPassword);
      statement = connection.createStatement();
      final String queryString = String
          .format(
              "SELECT up.write_permission FROM `plugin_svn_agent`.`SVN_USER` u, `plugin_svn_agent`.SVN_USER_PERMISSION up, `plugin_svn_agent`.`SVN_REPOSITORY_PATH` rp, `plugin_svn_agent`.`SVN_REPOSITORY` r WHERE u.id=up.user_id and rp.id=up.repository_path_id and rp.repository_id=r.id and u.name='%s' and r.name='%s'",
              pLogin, pLocation);

      rs = statement.executeQuery(queryString);
      if (rs.next())
      {
        if (pNeedWriteAccess)
        {
          result = rs.getBoolean(1);
        }
        else
        {
          result = true;
        }
      }
    }
    catch (final Exception e)
    {
      throw new ServletException("Unable to check user authorization", e);
    }
    finally
    {
      try
      {
        if (rs != null)
        {
          rs.close();
        }
        if (statement != null)
        {
          statement.close();
        }
        if (connection != null)
        {
          connection.close();
        }
      }
      catch (final SQLException e)
      {
        // Ignore this
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   * javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(final ServletRequest pServletRequest, final ServletResponse pServletResponse,
      final FilterChain filterChain) throws IOException, ServletException
  {
    final HttpServletRequest request = (HttpServletRequest) pServletRequest;
    final HttpServletResponse response = (HttpServletResponse) pServletResponse;

    // Check current request
    if (matchExcludePatterns(request))
    {
      filterChain.doFilter(request, response);
      return;
    }

    final State state = new State(request, response);
    final CredentialsManager credentialsManager = new CredentialsManager();
    UserCredentials userCredentials = null;
    try
    {
      userCredentials = credentialsManager.getUserCredentials(request, response);
      final String location = request.getParameter(RequestParameters.LOCATION);
      if (userCredentials != null)
      {
        final String login = userCredentials.getUsername();
        final boolean isAuthorized = isAuthorized(login, location, matchWriteAccesPatterns(request));
        Logger.getLogger(this.getClass()).debug(
            String.format("Login [%s] is %s for the path [%s]", login, isAuthorized ? "authorized"
                : "not authorized", location));
        if (isAuthorized)
        {
          filterChain.doFilter(pServletRequest, pServletResponse);
        }
        else
        {
          redirectToRestrictPage(state);
        }
      }
    }
    catch (final SVNWebClientException e)
    {
      throw new ServletException("Unable to get user credentials.", e);
    }
  }

  /**
   * @param pUrl
   * @return
   */
  private boolean matchExcludePatterns(final HttpServletRequest pRequest)
  {
    boolean matchExclude = false;
    for (final String pattern : EXCLUDES_PATTERN)
    {
      final String path = pRequest.getServletPath();
      if ((path != null) && (path.startsWith(pattern)))
      {
        matchExclude = true;
        break;
      }
    }
    return matchExclude;
  }

  private boolean matchWriteAccesPatterns(final HttpServletRequest pRequest)
  {
    boolean matchPattern = false;
    for (final String pattern : WRITE_ACCESS_PATTERN)
    {
      final String path = pRequest.getServletPath();
      if ((path != null) && (path.startsWith(pattern)))
      {
        matchPattern = true;
        break;
      }
    }
    return matchPattern;
  }

  private void redirectToRestrictPage(final State pState) throws ServletException
  {
    final String query = pState.getRequest().getQueryString();
    final String uri = pState.getRequest().getServletPath();
    pState.getRequest().getSession().setAttribute(SystemInitializing.ORIGINAL_URL, uri.substring(1));
    try
    {
      final String restrictUrl = Links.RESTRICTED_ACCESS + "?" + query;
      pState.getResponse().sendRedirect(restrictUrl);
    }
    catch (final IOException e)
    {
      throw new ServletException("Unable to redirect user to restricted page.", e);
    }
  }

}
