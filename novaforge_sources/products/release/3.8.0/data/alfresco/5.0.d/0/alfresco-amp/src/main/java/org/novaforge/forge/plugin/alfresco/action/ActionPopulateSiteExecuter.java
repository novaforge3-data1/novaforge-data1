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
package org.novaforge.forge.plugin.alfresco.action;

import java.util.List;

import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.utils.AlfrescoUtils;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 *
 *
 */
public class ActionPopulateSiteExecuter extends ActionExecuterAbstractBase
{

  private final Logger       logger      = Logger.getLogger(getClass());

  public static final String ACTION_NAME = "populate-site";

  public static final String PARAM_NOM   = "param-nom";
  public static final String PARAM_MODEL = "param-model";

  /**
   * le node service
   */
  private NodeService        nodeService;
  private FileFolderService  fileFolderService;
  private SearchService      searchService;

  /**
   * Set the node service
   * 
   * @param nodeService
   *          the node service
   */
  public void setNodeService(final NodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  /**
   * Set the FileFolderService
   * 
   * @param nodeService
   *          the FileFolderService
   */
  public void setFileFolderService(final FileFolderService fileFolderService)
  {
    this.fileFolderService = fileFolderService;
  }

  public void setSearchService(final SearchService searchService)
  {
    this.searchService = searchService;
  }

  /**
   * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
   */
  @Override
  protected void addParameterDefinitions(final List<ParameterDefinition> paramList)
  {

  }

  /**
   * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(Action, NodeRef)
   */
  @Override
  public void executeImpl(final Action action, final NodeRef actionedUponNodeRef)
  {
    // On vérifie si le noeud existe toujours
    if (this.nodeService.exists(actionedUponNodeRef))
    {
      // On applique la règle que sur les sites
      final QName TYPE_SITE = QName.createQName("http://www.alfresco.org/model/site/1.0", "site");

      if (TYPE_SITE.isMatch(this.nodeService.getType(actionedUponNodeRef)))
      {
        try
        {
          logger.debug("PopulateSite->start");
          final NodeRef nodeModel = getProjectModel();
          /*
           * List<ChildAssociationRef> children = this.nodeService.getChildAssocs(actionedUponNodeRef);
           * NodeRef doclib = null; for (ChildAssociationRef child : children) { if
           * ("documentLibrary".equals(this.nodeService.getProperty(child.getChildRef(),
           * ContentModel.PROP_NAME))) { doclib = child.getChildRef(); } }
           * if (doclib == null) { FileInfo documentlibrary =
           * fileFolderService.create(actionedUponNodeRef, "documentLibrary", ContentModel.TYPE_FOLDER);
           * doclib = documentlibrary.getNodeRef(); }
           */
          fileFolderService.copy(nodeModel, actionedUponNodeRef, "documentLibrary");
          logger.debug("PopulateSite->end");
        }
        catch (final FileExistsException e)
        {
          logger.error(Tools.getMessage(e), e);
        }
        catch (final Exception e)
        {
          logger.error(Tools.getMessage(e), e);
        }
      }
    }
  }

  /**
   * @throws Exception
   */
  private NodeRef getProjectModel() throws Exception
  {
    NodeRef returned = null;

    final String query = "PATH:\"/app:company_home/app:dictionary/app:space_templates/cm:Projet/.\"";
    ResultSet resultSet = AlfrescoUtils.executeQueryAdmin(searchService, query);
    if (resultSet == null || resultSet.length() < 1)
    { // project model doesn't exist : we need to create it
      final String rootModel = "PATH:\"/app:company_home/app:dictionary/app:space_templates/.\"";
      resultSet = AlfrescoUtils.executeQuery(searchService, rootModel);
      final NodeRef root = resultSet.getNodeRef(0);
      final FileInfo projectFileInfo = fileFolderService.create(root, "Projet", ContentModel.TYPE_FOLDER);
      returned = projectFileInfo.getNodeRef();
    }
    else
    {
      returned = resultSet.getNodeRef(0);
    }

    return returned;
  }
}
