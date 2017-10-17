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
package org.polarion.svnwebclient.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.polarion.svnwebclient.authorization.impl.CredentialsManager;
import org.polarion.svnwebclient.configuration.ConfigurationProvider;
import org.polarion.svnwebclient.configuration.WebConfigurationProvider;
import org.polarion.svnwebclient.web.SystemInitializing;

public class LogoutServlet extends HttpServlet
{

	private static final long serialVersionUID = -4086979556233654490L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	    throws ServletException, IOException
	{
		execute(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
	    throws ServletException, IOException
	{
		execute(request, response);
	}

	protected void execute(final HttpServletRequest request, final HttpServletResponse response)
	    throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		String id = (String) session.getAttribute(SystemInitializing.ID);

		session.setAttribute(SystemInitializing.ID, null);
		session.setAttribute(CredentialsManager.CREDENTIALS, null);
		session.invalidate();
		if ((id != null) && ConfigurationProvider.getInstance().isMultiRepositoryMode())
		{
			try
			{
				org.polarion.svnwebclient.data.javasvn.SVNRepositoryPool.getInstance(id).shutdown();
				org.polarion.svncommons.commentscache.SVNRepositoryPool.getInstance(id).shutdown();
			}
			catch (Exception e)
			{
				throw new ServletException(e);
			}
		}
		String casServerLogoutUrl = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.CAS_SERVER_LOGOUT_URL);
		String url = response.encodeRedirectURL(casServerLogoutUrl);
		response.sendRedirect(url);
	}
}
