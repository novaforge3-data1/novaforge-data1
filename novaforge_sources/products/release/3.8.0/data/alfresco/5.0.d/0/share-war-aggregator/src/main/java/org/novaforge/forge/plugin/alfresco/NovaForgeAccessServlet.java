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
package org.novaforge.forge.plugin.alfresco;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.novaforge.forge.plugins.ws.instance.PluginInstanceService;
import org.novaforge.forge.plugins.ws.instance.PluginInstanceServiceService;
import org.novaforge.forge.plugins.ws.instance.PluginServiceException_Exception;

/**
 * Servlet used for NovaForge Authentification
 */
public class NovaForgeAccessServlet extends HttpServlet
{
  /**
    * 
    */
  private static final long serialVersionUID = -7958895112114428721L;

  private final Logger      log              = Logger.getLogger(NovaForgeAccessServlet.class);

  /**
   * @see HttpServlet#HttpServlet()
   */
  public NovaForgeAccessServlet()
  {
    super();
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException,
      IOException
  {
    doPost(req, res);
  }

  @Override
  protected void doPost(final HttpServletRequest pRequest, final HttpServletResponse pResponse)
      throws ServletException, IOException
  {
    final String projectId = getSiteId(pRequest);
    if (projectId != null)
    {
      pResponse.sendRedirect(pRequest.getContextPath() + "/page/site-index?site=" + projectId);
    }
    else
    {
      pResponse.sendRedirect(pRequest.getContextPath() + "/page/site-index");

    }
  }

  private String getSiteId(final HttpServletRequest req) throws ServletException
  {
    final String instanceId = req.getParameter("instance_id");

    final PluginInstanceServiceService pluginInstanceServiceImpl = new PluginInstanceServiceService(
        getInstanceUrl(), new QName("http://services.plugins.core.forge.novaforge.org/",
            "PluginInstanceServiceService"));
    final PluginInstanceService instanceType = pluginInstanceServiceImpl.getPluginInstanceServicePort();
    String siteId = "";
    try
    {
      siteId = instanceType.getToolProjectId(instanceId);
    }
    catch (final PluginServiceException_Exception e)
    {
      log.error(String.format("Error occured when getting the site id from [instance id=%s]", instanceId), e);
      throw new ServletException(String.format(
          "Error occured when getting the site id from [instance id=%s]", instanceId), e);
    }

    if (log.isDebugEnabled())
    {
      log.debug(String.format("Getting the [site_id=%s] with [instance_is=%s]", siteId, instanceId));
    }

    return siteId;
  }

  private URL getInstanceUrl() throws ServletException
  {
    final String instanceUrlStr = System.getProperty("alfresco.novaforge.instance.wsdl",
        "http://127.0.0.1:8181/cxf/alfrescoInstance?wsdl");
    log.debug(String.format("Instance WSDL = %s", instanceUrlStr));
    URL instanceUrl = null;
    try
    {
      instanceUrl = new URL(instanceUrlStr);
    }
    catch (final MalformedURLException e)
    {
      log.error(e.getLocalizedMessage());
      throw new ServletException(e);
    }
    return instanceUrl;
  }
}
