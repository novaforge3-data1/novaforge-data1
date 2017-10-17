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
package org.novaforge.forge.plugin.alfresco.webscripts;

import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.utils.AlfrescoUtils;
import net.bull.alfresco.framework.webscript.AbstractAlfrescoWebScript;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.log4j.Logger;
import org.novaforge.forge.plugin.alfresco.exception.ForgeException;

public class InviteUser extends AbstractAlfrescoWebScript
{
   private String              userName;
   private String              projectName;
   private String              role;

   private final Logger        log = Logger.getLogger(getClass());

   private SearchService       searchService;
   private PermissionService   permissionService;

   @Override
   protected void initFromRequest() throws Exception
   {
      userName = req.getParameter("userName");
      projectName = req.getParameter("projectName");
      role = req.getParameter("role");

      // Initializing services
      fileFolderService = serviceRegistry.getFileFolderService();
      searchService = serviceRegistry.getSearchService();
      nodeService = serviceRegistry.getNodeService();

      checking();
   }

   /**
    * Checking parameters
    * 
    * @throws ForgeException
    */
   private void checking() throws Exception
   {
      boolean isValid = true;
      isValid = isValid && Tools.isSet(userName);
      isValid = isValid && Tools.isSet(projectName);
      isValid = isValid && Tools.isSet(role);

      if (!isValid)
      {
         throw new ForgeException(ForgeException.ERR_PARAM_FORGE);
      }

   }

   @Override
   protected void internalTraitement() throws Exception
   {
      log.debug("InviteUser()->Start");

      inviteUser();

      res.put("userName", userName);
      res.put("projectName", projectName);
      res.put("role", role);
      res.put("message", "OK");

      log.debug("InviteUser()->End");
   }

   /**
    * Associate a user to a project with a specific role
    * 
    * @return
    * @throws Exception
    */
   private void inviteUser() throws Exception
   {
      // Initializing services
      permissionService = serviceRegistry.getPermissionService();
      searchService = serviceRegistry.getSearchService();

      // Check if the project exist
      String query = "@cm\\:name:\"" + projectName + "\" AND TYPE:\"forge:project\"";
      ResultSet resultSet = AlfrescoUtils.executeQueryAdmin(searchService, query);
      if (!resultSet.getNodeRefs().isEmpty())
      {
         NodeRef projectRef = resultSet.getNodeRef(0);

         // Set the permission to the project
         permissionService.setPermission(projectRef, userName, role, true);
      }
      else
      {
         // le projet n'existe pas
         throw new ForgeException(ForgeException.ERR_PROJET_NOT_EXIST);
      }
   }
}
