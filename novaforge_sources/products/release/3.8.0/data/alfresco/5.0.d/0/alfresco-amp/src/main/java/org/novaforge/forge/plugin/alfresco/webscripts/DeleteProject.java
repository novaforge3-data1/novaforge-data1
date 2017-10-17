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

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.log4j.Logger;
import org.novaforge.forge.plugin.alfresco.exception.ForgeException;

public class DeleteProject extends AbstractAlfrescoWebScript
{
   private String        projectName;

   private final Logger        log = Logger.getLogger(getClass());
   private NodeService         nodeService;
   private SearchService       searchService;

   @Override
   protected void initFromRequest() throws Exception
   {
      projectName = req.getParameter("projectName");

      // Initializing services
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
      isValid = isValid && Tools.isSet(projectName);

      if (!isValid)
      {
         throw new ForgeException(ForgeException.ERR_PARAM_FORGE);
      }
   }

   @Override
   protected void internalTraitement() throws Exception
   {
      log.debug("DeleteProject()->Start");

      deleteProject();

      res.put("projectName", projectName);
      res.put("message", "OK");

      log.debug("DeleteProject()->End");
   }

   /**
    * Delete a project
    * 
    * @return
    * @throws Exception
    */
   private void deleteProject() throws Exception
   {
      // On vérifie si un projet existe déjà avec ce nom
      String query = "@cm\\:name:\"" + projectName + "\" AND TYPE:\"forge:project\"";
      ResultSet resultSet = AlfrescoUtils.executeQueryAdmin(searchService, query);
      if (!resultSet.getNodeRefs().isEmpty())
      {
         // delete the project
         nodeService.deleteNode(resultSet.getNodeRef(0));
      }
      else
      {
         throw new ForgeException(ForgeException.ERR_PROJET_NOT_EXIST);
      }
   }

}
