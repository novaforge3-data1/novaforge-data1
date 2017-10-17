/*
 * Copyright (c) 2004, 2005 Polarion Software, All rights reserved.
 * Copyright (C) 2011-2014, BULL SAS, NovaForge Version 3 and above.
 * Email: community@polarion.org
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 (the "License"). You may not use
 * this file except in compliance with the License. Copy of the License is
 * located in the file LICENSE.txt in the project distribution. You may also
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * POLARION SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. POLARION SOFTWARE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package org.polarion.svnwebclient.authorization.impl;

import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.polarion.svnwebclient.SVNWebClientException;
import org.polarion.svnwebclient.authorization.ICredentialsManager;
import org.polarion.svnwebclient.authorization.UserCredentials;
import org.polarion.svnwebclient.configuration.ConfigurationProvider;
import org.polarion.svnwebclient.configuration.WebConfigurationProvider;
import org.polarion.svnwebclient.data.javasvn.DataProvider;
import org.polarion.svnwebclient.web.SystemInitializing;
import org.polarion.svnwebclient.web.resource.Links;
import org.polarion.svnwebclient.web.support.AbstractRequestHandler;
import org.polarion.svnwebclient.web.support.RequestHandler;
import org.tmatesoft.svn.core.SVNException;

/**
 * @author <A HREF="mailto:svnbrowser@polarion.org">Polarion Software </A>
 */
public class CredentialsManager implements ICredentialsManager
{
  public static final String CREDENTIALS = "credentials";

  @Override
  public UserCredentials getUserCredentials(final HttpServletRequest request,
      final HttpServletResponse response) throws SVNWebClientException
  {
    UserCredentials credentials = (UserCredentials) request.getSession().getAttribute(
        CredentialsManager.CREDENTIALS);
    String username = null;
    String password = null;
    final AbstractRequestHandler requestHandler = getRequestHandler(request);

    if (credentials == null)
    {
      // BasicAuth mode
      if (ConfigurationProvider.getInstance().isBasicAuth() && isBasicAuthentication(request))
      {
        final UserCredentials basicCredentials = getBasicAuthenticationCredentials(request);
        username = basicCredentials.getUsername();
        password = basicCredentials.getPassword();
      }// download manager calls
      else if (isBasicAuthentication(request))
      {
        final UserCredentials basicCredentials = getBasicAuthenticationCredentials(request);
        if (basicCredentials != null)
        {
          username = basicCredentials.getUsername();
          password = basicCredentials.getPassword();
        }
      }// CAS Mode
      else if (isCASAuthentication(request))
      {
        username = request.getRemoteUser();
        password = null;
        if (username != null)
        {
          request.getSession().setAttribute(ICredentialsManager.IS_LOGGED_IN, "exist");
        }
      }
      else
      {
        username = requestHandler.getUsername();
        password = requestHandler.getPassword();
      }

      credentials = new UserCredentials(username, password);
      request.getSession().setAttribute(CredentialsManager.CREDENTIALS, credentials);

      final String url = getRepositoryLocation(requestHandler);
      try
      {
        Logger.getLogger(this.getClass()).debug(
            "DataProvider Verify[" + url + ", " + credentials.getUsername() + ", "
                + credentials.getPassword() + "]");
        DataProvider.verify(url, credentials.getUsername(), credentials.getPassword());
        Logger.getLogger(this.getClass()).debug("DataProvider Verified");
        if ("".equals(password)
            && (request.getSession().getAttribute(ICredentialsManager.IS_LOGGED_IN) != null))
        {
          forceCredentialsRequest(request, response);
          return null;
        }
        else
        {
          request.getSession().setAttribute(ICredentialsManager.IS_LOGGED_IN, "exist");
        }
      }
      catch (final SVNException se)
      {
        // to remove
        se.printStackTrace();
        Logger.getLogger(this.getClass()).debug(
            "It's not allowed to enter, your credentials:\t" + "username: " + credentials.getUsername()
                + ", " + "url: " + url);
        forceCredentialsRequest(request, response);
        return null;
      }
    }

    Logger.getLogger(this.getClass()).debug("Credentials: \nUsername: " + credentials.getUsername());
    return credentials;
  }

  private boolean isCASAuthentication(final HttpServletRequest request)
  {
    final String casAuthStr = WebConfigurationProvider.getInstance().getParameter(
        WebConfigurationProvider.CAS_AUTH);
    return "true".equals(casAuthStr);

  }

  protected boolean isBasicAuthentication(final HttpServletRequest request)
  {
    final String auth = request.getHeader("Authorization");
    if ((auth != null) && !auth.equals("") && auth.toLowerCase().startsWith("basic"))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  protected AbstractRequestHandler getRequestHandler(final HttpServletRequest request)
  {
    return new RequestHandler(request);
  }

  protected String getRepositoryLocation(final AbstractRequestHandler requestHandler)
  {
    String res = "";
    if (!ConfigurationProvider.getInstance().isMultiRepositoryMode())
    {
      res = ConfigurationProvider.getInstance().getRepositoryUrl();
    }
    else
    {
      res += ConfigurationProvider.getInstance().getParentRepositoryDirectory() + "/"
          + requestHandler.getRepositoryName();
    }
    return res;
  }

  protected UserCredentials getBasicAuthenticationCredentials(final HttpServletRequest request)
  {
    UserCredentials res = null;
    String auth = request.getHeader("Authorization");
    auth = auth.substring(auth.lastIndexOf(" ") + 1, auth.length());
    final String authStr = new String(Base64.decodeBase64(auth.getBytes()));
    final StringTokenizer stringtokenizer = new StringTokenizer(authStr, ":");
    if (stringtokenizer.hasMoreTokens())
    {
      res = new UserCredentials();
      res.setUsername(stringtokenizer.nextToken().toLowerCase());
    }
    if (stringtokenizer.hasMoreTokens())
    {
      res.setPassword(stringtokenizer.nextToken());
    }
    return res;
  }

  protected void forceCredentialsRequest(final HttpServletRequest request, final HttpServletResponse response)
  {
    request.getSession().setAttribute(CredentialsManager.CREDENTIALS, null);
    request.getSession().setAttribute(SystemInitializing.ID, null);
    try
    {
      if (ConfigurationProvider.getInstance().isBasicAuth())
      {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "BASIC realm=\""
            + ConfigurationProvider.getInstance().getBasicRealm() + "\"");
        response.sendError(401);
      }
      else
      {
        final RequestDispatcher dispatcher = request.getRequestDispatcher(Links.LOGIN);
        dispatcher.forward(request, response);
      }
    }
    catch (final Exception e)
    {
      Logger.getLogger(this.getClass()).error(e, e);
    }
  }

}
