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
package org.polarion.svnwebclient.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.polarion.svnwebclient.SVNWebClientException;
import org.polarion.svnwebclient.authorization.UserCredentials;
import org.polarion.svnwebclient.authorization.impl.CredentialsManager;
import org.polarion.svnwebclient.configuration.ConfigurationProvider;
import org.polarion.svnwebclient.data.DataProviderException;
import org.polarion.svnwebclient.data.DataProviderFactory;
import org.polarion.svnwebclient.data.IDataProvider;
import org.polarion.svnwebclient.data.javasvn.DataProvider;
import org.polarion.svnwebclient.web.model.LinkProviderFactory;
import org.polarion.svnwebclient.web.resource.Links;
import org.polarion.svnwebclient.web.support.AbstractRequestHandler;
import org.polarion.svnwebclient.web.support.RequestParameters;
import org.polarion.svnwebclient.web.support.State;
import org.tmatesoft.svn.core.SVNException;

public class SystemInitializing
{
	public static final String ORIGINAL_URL     = "originalUrl";
	public static final String ID               = "id";
	protected State            state;
	protected boolean          isPickerInstance = false;
	protected boolean          isSingleRevision;
	protected boolean          isMultiSelectionUrl;

	public IDataProvider init(final HttpServletRequest request, final HttpServletResponse response,
	    final AbstractRequestHandler requestHandler) throws SVNWebClientException
	{
		if (ConfigurationProvider.getInstance().getConfigurationError().isError())
		{
			throw new SVNWebClientException(ConfigurationProvider.getInstance().getConfigurationError()
			    .getException());
		}

		state = new State(request, response);
		CredentialsManager credentialsManager = new CredentialsManager();
		UserCredentials userCredentials = null;

		userCredentials = credentialsManager.getUserCredentials(request, response);
		if (userCredentials == null)
		{
			return null;
		}

		String id = (String) request.getSession().getAttribute(SystemInitializing.ID);
		String url = getRepositoryLocation(requestHandler);
		try
		{
			id = DataProvider.getID(url, userCredentials.getUsername(), userCredentials.getPassword());
			if (ConfigurationProvider.getInstance().isMultiRepositoryMode())
			{
				DataProvider.startup(userCredentials.getUsername(), userCredentials.getPassword(), id, url);
			}
		}
		catch (DataProviderException de)
		{
			throw new SVNWebClientException(de);

		}
		catch (SVNException se)
		{
			throw new SVNWebClientException(se);
		}
		request.getSession().setAttribute(SystemInitializing.ID, id);
		checkRestrictedAccess();

		IDataProvider dataProvider = DataProviderFactory.getDataProvider();
		dataProvider.setRelativeLocation(id, requestHandler.getRepositoryName());
		dataProvider.connect(userCredentials, id, url);
		return dataProvider;
	}

	protected void checkRestrictedAccess()
	{
		String originalUrl = (String) state.getRequest().getSession()
		    .getAttribute(SystemInitializing.ORIGINAL_URL);
		if (originalUrl != null)
		{
			String currentUri = state.getRequest().getServletPath().substring(1);
			if (!originalUrl.equals(currentUri))
			{
				AttributeStorage.getInstance().cleanSession(state.getRequest().getSession());
			}
		}
	}

	public void redirectToRestrictPage()
	{
		String query = state.getRequest().getQueryString();
		String uri = state.getRequest().getServletPath();
		state.getRequest().getSession().setAttribute(SystemInitializing.ORIGINAL_URL, uri.substring(1));
		try
		{
			String restrictUrl = Links.RESTRICTED_ACCESS + "?" + query;
			if (isPickerInstance)
			{
				restrictUrl += "&" + RequestParameters.CONTENT_MODE_TYPE + "="
				    + LinkProviderFactory.PICKER_CONTENT_MODE_VALUE;
			}
			state.getResponse().sendRedirect(restrictUrl);
		}
		catch (IOException e)
		{
		}
	}

	protected String getRepositoryLocation(final AbstractRequestHandler requestHandler)
	{
		return ConfigurationProvider.getInstance().getRepositoryLocation(requestHandler.getRepositoryName());
	}

	public void setIsPickerInstance()
	{
		isPickerInstance = true;
	}

	public void setPickerFields(final boolean isSingleRevision, final boolean isMultiSelectionUrl)
	{
		this.isSingleRevision = isSingleRevision;
		this.isMultiSelectionUrl = isMultiSelectionUrl;
	}
}
