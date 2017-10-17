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

import java.util.List;

import net.bull.alfresco.framework.webscript.AbstractAlfrescoWebScript;

import org.alfresco.service.cmr.security.PermissionService;
import org.apache.log4j.Logger;

public class GetSiteRoles extends AbstractAlfrescoWebScript
{
   private final Logger        log = Logger.getLogger(getClass());
   protected PermissionService permissionService;

   @Override
   protected void initFromRequest() throws Exception
   {
      // Initializing services
      // permissionService = serviceRegistry.getPermissionService();

   }

   @Override
   protected void internalTraitement() throws Exception
   {
      log.debug("GetSiteRoles()->Start");

      List<String> roles = serviceRegistry.getSiteService().getSiteRoles();

      res.put("roles", roles.toString());
      res.put("message", "OK");

      log.debug("GetSiteRoles()->End");
   }
}
