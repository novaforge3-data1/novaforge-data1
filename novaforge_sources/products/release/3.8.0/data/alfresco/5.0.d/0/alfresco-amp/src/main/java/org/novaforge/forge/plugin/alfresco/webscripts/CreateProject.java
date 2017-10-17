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

import java.util.ArrayList;
import java.util.List;

import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.utils.AlfrescoUtils;
import net.bull.alfresco.framework.webscript.AbstractAlfrescoWebScript;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.novaforge.forge.plugin.alfresco.exception.ForgeException;
import org.springframework.extensions.surf.PresetsManager;

public class CreateProject extends AbstractAlfrescoWebScript
{
  private String            name;
  private String            parentName;
  private NodeRef           parentRef;

  private final Logger      log = Logger.getLogger(this.getClass());
  private NodeService       nodeService;
  private SearchService     searchService;
  private FileFolderService fileFolderService;
  private PresetsManager    presetsManager;

  @Override
  protected void initFromRequest() throws Exception
  {
    this.name = this.req.getParameter("name");
    this.parentName = this.req.getParameter("parentName");

    // Initializing services
    this.fileFolderService = this.serviceRegistry.getFileFolderService();
    this.searchService = this.serviceRegistry.getSearchService();
    this.nodeService = this.serviceRegistry.getNodeService();
    this.checking();
  }

  /**
   * Checking parameters
   * 
   * @throws ForgeException
   */
  private void checking() throws Exception
  {
    boolean isValid = true;
    isValid = isValid && Tools.isSet(this.name);
    // isValid = isValid && Tools.isSet(parentName);

    if (!isValid)
    {
      throw new ForgeException(ForgeException.ERR_PARAM_FORGE);
    }

    if (!Tools.isSet(this.parentName))
    {
      this.parentRef = repositoryHelper.getCompanyHome();
    }
    else
    {
      final List<String> list = new ArrayList<String>();
      list.add(this.parentName);
      final FileInfo fInfo = this.fileFolderService.resolveNamePath(repositoryHelper.getCompanyHome(), list);
      this.parentRef = fInfo.getNodeRef();
    }
  }

  @Override
  protected void internalTraitement() throws Exception
  {
    this.log.debug("CreateProject()->Start");

    final NodeRef newProjectRef = this.createProject();
    final String newProjectName = (String) this.nodeService
        .getProperty(newProjectRef, ContentModel.PROP_NAME);
    this.res.put("projectName", newProjectName);
    this.res.put("message", "OK");

    this.log.debug("CreateProject()->End");
  }

  /**
   * Create a project: - Copy project's model - Specialize the type forge:project
   * 
   * @return
   * @throws Exception
   */
  private NodeRef createProject() throws Exception
  {
    // On vérifie si un projet existe déjà avec ce nom
    final String query = "@cm\\:name:\"" + this.name + "\" AND TYPE:\"forge:project\"";
    final ResultSet resultSet = AlfrescoUtils.executeQueryAdmin(this.searchService, query);
    if (!resultSet.getNodeRefs().isEmpty())
    {
      throw new ForgeException(ForgeException.ERR_PROJET_EXIST);
    }

    // copie du modele de projet
    this.log.debug("CreateProject()->name : " + this.name);
    final NodeRef nodeModel = this.getModeleProject();
    this.log.debug("CreateMarche()->MODEL : " + nodeModel);
    // création du projet par recopie
    final FileInfo newProject = this.fileFolderService.copy(nodeModel, this.parentRef, this.name);

    // spécialisation du type
    final QName TYPE_PROJECT = QName.createQName("http://wwww.bull.net/model/forge", "project");
    this.nodeService.setType(newProject.getNodeRef(), TYPE_PROJECT);

    return newProject.getNodeRef();
  }

  private NodeRef getModeleProject() throws Exception
  {
    // PATH:"/app:company_home/app:dictionary/app:space_templates/cm:Projet/."
    final String query = "PATH:\"/app:company_home/app:dictionary/app:space_templates/cm:Projet/.\"";
    final ResultSet resultSet = AlfrescoUtils.executeQueryAdmin(this.searchService, query);
    final NodeRef node = resultSet.getNodeRef(0);
    if (node == null)
    {
      throw new ForgeException();
    }
    return node;
  }
}
