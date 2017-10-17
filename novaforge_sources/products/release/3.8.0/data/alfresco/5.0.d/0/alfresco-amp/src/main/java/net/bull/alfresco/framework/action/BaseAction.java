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
package net.bull.alfresco.framework.action;

import java.util.List;

import net.bull.alfresco.framework.type.ISearchNode;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.log4j.Logger;

/**
 * Classe de base pour écrire un action.
 * Dérive de {@link ActionExecuterAbstractBase}
 * <p>
 * Fournit :
 * <ul>
 * <li>nodeService</li>
 * <li>fileFolderService</li>
 * <li>searchService</li>
 * <li>contentService</li>
 * </p>
 * 
 * @author Rouviere_x
 */
public abstract class BaseAction extends ActionExecuterAbstractBase
{

  protected FileFolderService  fileFolderService;

  protected NodeService        nodeService;

  protected SearchService      searchService;

  protected ContentService     contentService;

  protected ServiceRegistry    serviceRegistry;

  protected TransactionService transactionService;

  protected ISearchNode        searchNode;

  protected Logger             log = Logger.getLogger(getClass());

  protected Action             action;

  protected NodeRef            currentNode;

  protected NodeRef            companyHome;

  private final String         name;

  /**
   * Action de base
   */
  public BaseAction(final String name)
  {
    super(); // je suis super content ça sert à rien ici ;)
    this.name = name;
  }

  public BaseAction()
  {
    this("BaseAction");
  }

  public FileFolderService getFileFolderService()
  {
    return fileFolderService;
  }

  public void setFileFolderService(final FileFolderService fileFolderService)
  {
    this.fileFolderService = fileFolderService;
  }

  public NodeService getNodeService()
  {
    return nodeService;
  }

  public void setNodeService(final NodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public SearchService getSearchService()
  {
    return searchService;
  }

  public void setSearchService(final SearchService searchService)
  {
    this.searchService = searchService;
  }

  public ContentService getContentService()
  {
    return contentService;
  }

  public void setContentService(final ContentService contentService)
  {
    this.contentService = contentService;
  }

  /**
   * Traitement interne.
   * Abstract ?
   */
  protected abstract void internalTraitement();

  @Override
  protected void executeImpl(final Action action, final NodeRef actionedUponNodeRef)
  {
    log.debug("BaseAction::run()-> Début");
    this.action = action;
    this.currentNode = actionedUponNodeRef;
    // companyHome = AlfrescoUtils.getCompanyHome(searchService);
    transactionService = serviceRegistry.getTransactionService();
    internalTraitement();
    log.debug("BaseAction::run()-> Fin");

  }

  @Override
  protected void addParameterDefinitions(final List<ParameterDefinition> paramList)
  {
    // TODO Auto-generated method stub

  }

  public void setServiceRegistry(final ServiceRegistry serviceRegistry)
  {
    this.serviceRegistry = serviceRegistry;
  }

  public ISearchNode getSearchNode()
  {
    return searchNode;
  }

  public void setSearchNode(final ISearchNode searchNode)
  {
    this.searchNode = searchNode;
  }

}
