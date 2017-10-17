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
package org.polarion.svnwebclient.configuration;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.polarion.svncommons.commentscache.configuration.ProxySettings;
import org.polarion.svnwebclient.decorations.IAlternativeViewProvider;
import org.polarion.svnwebclient.decorations.IAuthorDecorator;
import org.polarion.svnwebclient.decorations.IRevisionDecorator;

/**
 * @author <A HREF="mailto:svnbrowser@polarion.org">Polarion Software </A>
 */
public class ConfigurationProvider implements IConfigurationProvider
{
	protected static ConfigurationProvider instance;

	protected IRevisionDecorator           revisionDecorator;
	protected IAlternativeViewProvider     alternativeViewProvider;
	protected IAuthorDecorator             authorDecorator;

	protected ConfigurationError           error = new ConfigurationError();

	private ConfigurationProvider()
	{
	}

	@Override
	public ConfigurationError getConfigurationError()
	{
		return error;
	}

	public static synchronized ConfigurationProvider getInstance()
	{
		if (ConfigurationProvider.instance == null)
		{
			ConfigurationProvider.instance = new ConfigurationProvider();
		}
		return ConfigurationProvider.instance;
	}

	@Override
	public void checkConfiguration() throws ConfigurationException
	{
		String parentUrl = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PARENT_REPOSITORY_DIRECTORY);
		String url = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.REPOSITORY_URL);
		if (((parentUrl == null) && (url == null)) || ((parentUrl != null) && (url != null)))
		{
			throw new ConfigurationException(
			    "You have to specify only either ParentRepositoryDirectory or RepositoryUrl in web.xml, don't mix them.");
		}
		if (url != null)
		{
			checkNotNullOrEmpty(WebConfigurationProvider.REPOSITORY_URL);
		}
		else if (parentUrl != null)
		{
			checkNotNullOrEmpty(WebConfigurationProvider.PARENT_REPOSITORY_DIRECTORY);
		}

		checkLong(WebConfigurationProvider.SVN_CONNECTIONS_COUNT);
		checkLong(WebConfigurationProvider.VERSIONS_COUNT);

		checkBoolean(WebConfigurationProvider.PATH_AUTODETECT);
		if (isPathAutodetect())
		{
			checkNotNullOrEmpty(WebConfigurationProvider.TRUNK_NAME);
			checkNotNullOrEmpty(WebConfigurationProvider.BRANCHES_NAME);
			checkNotNullOrEmpty(WebConfigurationProvider.TAGS_NAME);
		}

		checkNotNullOrEmpty(WebConfigurationProvider.DEFAULT_ENCODING);

		checkLong(WebConfigurationProvider.CACHE_PAGE_SIZE);
		checkLong(WebConfigurationProvider.CACHE_PREFETCH_MESSAGES_COUNT);

		if (WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.SHOW_STACK_TRACE) != null)
		{
			checkBoolean(WebConfigurationProvider.SHOW_STACK_TRACE);
		}

		checkBoolean(WebConfigurationProvider.EMBEDDED);
		checkBoolean(WebConfigurationProvider.HIDE_POLARION_COMMIT);
		checkBoolean(WebConfigurationProvider.LOGOUT);

		checkBoolean(WebConfigurationProvider.BASIC_AUTH);
		if (isBasicAuth())
		{
			checkNotNullOrEmpty(WebConfigurationProvider.BASIC_REALM);
		}

		checkNotNullOrEmpty(WebConfigurationProvider.REVISION_DECORATOR);
		checkNotNullOrEmpty(WebConfigurationProvider.ALTERNATIVE_VIEW_PROVIDER);

		if (IConfigurationProvider.SVN_SSH == getProtocolType())
		{
			checkInt(WebConfigurationProvider.PROTOCOL_PORT_NUMBER);
			checkNotNull(WebConfigurationProvider.USERNAME);
			if (getProtocolKeyFile() != null)
			{
				checkNotNull(WebConfigurationProvider.PROTOCOL_PASS_PHRASE);
				checkNotNullOrEmpty(WebConfigurationProvider.PROTOCOL_KEY_FILE);
			}
			else
			{
				checkNotNull(WebConfigurationProvider.PASSWORD);
			}
		}
		else if (IConfigurationProvider.SSL == getProtocolType())
		{
			checkNotNull(WebConfigurationProvider.PROTOCOL_PASS_PHRASE);
			checkNotNullOrEmpty(WebConfigurationProvider.PROTOCOL_KEY_FILE);
		}
		else
		{
			// http, svn
			if (url != null)
			{
				checkNotNull(WebConfigurationProvider.USERNAME);
				checkNotNull(WebConfigurationProvider.PASSWORD);
			}
		}
		checkBoolean(WebConfigurationProvider.PROXY_SUPPORTED);
		if (new Boolean(WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PROXY_SUPPORTED)).booleanValue() == true)
		{
			checkInt(WebConfigurationProvider.PROXY_PORT_NUMBER);
			checkNotNullOrEmpty(WebConfigurationProvider.PROXY_HOST);
		}
	}

	public String getProductVersion()
	{
		return "";
	}

	@Override
	public String getEmailFrom()
	{
		String emailFrom = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.EMAIL_FROM);
		return emailFrom;
	}

	@Override
	public String getEmailTo()
	{
		String emailTo = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.EMAIL_TO);
		return emailTo;
	}

	@Override
	public String getEmailHost()
	{
		String emailHost = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.EMAIL_HOST);
		return emailHost;
	}

	@Override
	public String getEmailPort()
	{
		String emailPort = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.EMAIL_PORT);
		return emailPort;
	}

	@Override
	public String getEmailProject()
	{
		String emailProject = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.EMAIL_PROJECT_NAME);
		return emailProject;
	}

	@Override
	public boolean isEmbedded()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.EMBEDDED);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isEmbeddedNovaforge()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.EMBEDDED_NOVAFORGE);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isBasicAuth()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.BASIC_AUTH);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isForcedHttpAuth()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.FORCED_HTTP_AUTH);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isHidePolarionCommit()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.HIDE_POLARION_COMMIT);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String getBasicRealm()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.BASIC_REALM);
		return value.trim();
	}

	@Override
	public String getParentRepositoryDirectory()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PARENT_REPOSITORY_DIRECTORY);
		if (value == null)
		{
			return null;
		}
		else
		{
			value = value.trim();
			if (value.endsWith("/"))
			{
				value = value.substring(0, value.length() - 1);
			}
			return value.trim();
		}
	}

	public boolean isMultiRepositoryMode()
	{
		return getParentRepositoryDirectory() == null ? false : true;
	}

	@Override
	public String getRepositoryUrl()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.REPOSITORY_URL);
		if (value.endsWith("/"))
		{
			value = value.substring(0, value.length() - 1);
		}
		return value.trim();
	}

	@Override
	public String getUsername()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.USERNAME);
		return value.trim();
	}

	@Override
	public String getPassword()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.PASSWORD);
		return value.trim();
	}

	@Override
	public long getSvnConnectionsCount()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.SVN_CONNECTIONS_COUNT);
		return Long.parseLong(value);
	}

	@Override
	public String getTempDirectory()
	{
		String result = null;
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.TEMP_DIRECTORY);
		if (value == null)
		{
			result = getOSTempDir();
			int index = result.lastIndexOf(System.getProperty("file.separator"));
			if (index != -1)
			{
				result = result.substring(0, index);
			}
		}
		else
		{
			result = value.trim();
		}
		return result;
	}

	@Override
	public long getVersionsCount()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.VERSIONS_COUNT);
		return Long.parseLong(value);
	}

	@Override
	public boolean isPathAutodetect()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PATH_AUTODETECT);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String getTrunkName()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.TRUNK_NAME);
		if (value != null)
		{
			value = value.trim();
		}
		return value;
	}

	@Override
	public String getBranchesName()
	{
		String value = WebConfigurationProvider.getInstance()
		    .getParameter(WebConfigurationProvider.BRANCHES_NAME);
		if (value != null)
		{
			value = value.trim();
		}
		return value;
	}

	@Override
	public String getTagsName()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.TAGS_NAME);
		if (value != null)
		{
			value = value.trim();
		}
		return value;
	}

	@Override
	public String getDefaultEncoding()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.DEFAULT_ENCODING);
		return value.trim();
	}

	/**
	 * @return Returns the zip encoding.
	 */
	@Override
	public String getZipEncoding()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.ZIP_ENCODING);
		return value.trim();
	}

	protected void checkZipEncoding() throws ConfigurationException
	{
		checkNotNullOrEmpty(WebConfigurationProvider.ZIP_ENCODING);
	}

	@Override
	public boolean isShowStackTrace()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.SHOW_STACK_TRACE);
		value = value.trim();
		if (WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String getCacheDirectory()
	{
		String result = null;
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.CACHE_DIRECTORY);
		if (value == null)
		{
			String tmpDir = getOSTempDir();
			result = tmpDir + "cache";
		}
		else
		{
			result = value.trim();
		}
		return result;
	}

	@Override
	public long getCachePageSize()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.CACHE_PAGE_SIZE);
		return Long.parseLong(value);
	}

	@Override
	public long getCachePrefetchMessagesCount()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.CACHE_PREFETCH_MESSAGES_COUNT);
		return Long.parseLong(value);
	}

	@Override
	public boolean isLogout()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.LOGOUT);
		return new Boolean(value).booleanValue();
	}

	@Override
	public String getProtocolKeyFile()
	{
		return WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.PROTOCOL_KEY_FILE);
	}

	@Override
	public String getProtocolPassPhrase()
	{
		return WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.PROTOCOL_PASS_PHRASE);
	}

	@Override
	public int getProtocolPortNumber()
	{
		String value = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PROTOCOL_PORT_NUMBER);
		return Integer.parseInt(value);
	}

	@Override
	public ProxySettings getProxy()
	{
		ProxySettings proxy = new ProxySettings();
		String isSupported = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.PROXY_SUPPORTED);
		proxy.setProxySupported(new Boolean(isSupported).booleanValue());
		if (new Boolean(isSupported).booleanValue() == true)
		{
			proxy.setHost(WebConfigurationProvider.getInstance().getParameter(WebConfigurationProvider.PROXY_HOST));
			proxy.setUserName(WebConfigurationProvider.getInstance().getParameter(
			    WebConfigurationProvider.PROXY_USER_NAME));
			proxy.setPassword(WebConfigurationProvider.getInstance().getParameter(
			    WebConfigurationProvider.PROXY_PASSWORD));
			String port = WebConfigurationProvider.getInstance().getParameter(
			    WebConfigurationProvider.PROXY_PORT_NUMBER);
			if (port != null)
			{
				proxy.setPort(Integer.parseInt(port));
			}
		}
		return proxy;
	}

	@Override
	public IRevisionDecorator getRevisionDecorator()
	{
		if (revisionDecorator == null)
		{
			revisionDecorator = (IRevisionDecorator) instantiate(WebConfigurationProvider.getInstance()
			    .getParameter(WebConfigurationProvider.REVISION_DECORATOR));
		}
		return revisionDecorator;
	}

	@Override
	public IAlternativeViewProvider getAlternativeViewProvider()
	{
		if (alternativeViewProvider == null)
		{
			alternativeViewProvider = (IAlternativeViewProvider) instantiate(WebConfigurationProvider.getInstance()
			    .getParameter(WebConfigurationProvider.ALTERNATIVE_VIEW_PROVIDER));
		}
		return alternativeViewProvider;
	}

	@Override
	public IAuthorDecorator getAuthorDecorator()
	{
		if (authorDecorator == null)
		{
			authorDecorator = (IAuthorDecorator) instantiate(WebConfigurationProvider.getInstance().getParameter(
			    WebConfigurationProvider.AUTHOR_DECORATOR));
		}
		return authorDecorator;
	}

	@Override
	public int getProtocolType()
	{
		String url = null;
		if (isMultiRepositoryMode())
		{
			url = getParentRepositoryDirectory();
		}
		else
		{
			url = getRepositoryUrl();
		}

		if (url.indexOf("svn+ssh://") != -1)
		{
			return IConfigurationProvider.SVN_SSH;
		}
		else if (url.indexOf("https://") != -1)
		{
			return IConfigurationProvider.SSL;
		}
		else
		{
			return IConfigurationProvider.HTTP;
		}
	}

	protected void checkNotNull(final String parameterName) throws ConfigurationException
	{
		String value = WebConfigurationProvider.getInstance().getParameter(parameterName);
		if (value == null)
		{
			throw new ConfigurationException(parameterName + " configuration parameter must be defined");
		}
	}

	protected Object instantiate(final String className)
	{
		try
		{
			Class clazz = Class.forName(className);
			return clazz.newInstance();
		}
		catch (ClassNotFoundException e)
		{
			Logger.getLogger(this.getClass()).error(e, e);
		}
		catch (InstantiationException e)
		{
			Logger.getLogger(this.getClass()).error(e, e);
		}
		catch (IllegalAccessException e)
		{
			Logger.getLogger(this.getClass()).error(e, e);
		}
		return null;
	}

	protected void checkNotNullOrEmpty(final String parameterName) throws ConfigurationException
	{
		String value = WebConfigurationProvider.getInstance().getParameter(parameterName);
		if (value == null)
		{
			throw new ConfigurationException(parameterName + " configuration parameter must be defined");
		}
		value = value.trim();
		if (value.length() == 0)
		{
			throw new ConfigurationException("Invalid value \"" + value + "\" of " + parameterName
			    + " configuration parameter. " + "It must be not empty string");
		}
	}

	protected void checkBoolean(final String parameterName) throws ConfigurationException
	{
		String value = WebConfigurationProvider.getInstance().getParameter(parameterName);
		if (value == null)
		{
			throw new ConfigurationException(parameterName + " configuration parameter must be defined");
		}
		value = value.trim();
		if (!(WebConfigurationProvider.VALUE_TRUE.equalsIgnoreCase(value) || WebConfigurationProvider.VALUE_FALSE
		    .equalsIgnoreCase(value)))
		{
			throw new ConfigurationException("Invalid value \"" + value + "\" of " + parameterName
			    + " configuration parameter. " + "Only " + WebConfigurationProvider.VALUE_TRUE + " and "
			    + WebConfigurationProvider.VALUE_FALSE + " are allowed");
		}
	}

	protected void checkLong(final String parameterName) throws ConfigurationException
	{
		String value = WebConfigurationProvider.getInstance().getParameter(parameterName);
		if (value == null)
		{
			throw new ConfigurationException(parameterName + " configuration parameter must be defined");
		}

		try
		{
			Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			throw new ConfigurationException("Invalid value \"" + value + "\" of " + parameterName
			    + " configuration parameter. " + "It must be numeric");
		}
	}

	protected void checkInt(final String parameterName) throws ConfigurationException
	{
		String value = WebConfigurationProvider.getInstance().getParameter(parameterName);
		if (value == null)
		{
			throw new ConfigurationException(parameterName + " configuration parameter must be defined");
		}

		try
		{
			Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			throw new ConfigurationException("Invalid value \"" + value + "\" of " + parameterName
			    + " configuration parameter. " + "It must be numeric");
		}
	}

	protected String getOSTempDir()
	{
		String tempdir = System.getProperty("java.io.tmpdir");
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\")))
		{
			tempdir = tempdir + System.getProperty("file.separator");
		}
		return tempdir;
	}

	@Override
	public String getRepositoryLocation(final String repositoryName)
	{
		String res = "";
		if (!isMultiRepositoryMode())
		{
			res = getRepositoryUrl();
		}
		else
		{
			res += getParentRepositoryDirectory() + "/" + repositoryName;
		}
		return res;
	}

	@Override
	public Set getCharacterEncodings()
	{
		Set res = new HashSet();

		String strEncodings = WebConfigurationProvider.getInstance().getParameter(
		    WebConfigurationProvider.CHARACTER_ENCODINGS);
		if (strEncodings != null)
		{
			strEncodings = strEncodings.trim();

			String[] encodings = strEncodings.split(",");
			for (String encoding : encodings)
			{
				res.add(encoding.trim());
			}
		}
		return res;
	}

}
