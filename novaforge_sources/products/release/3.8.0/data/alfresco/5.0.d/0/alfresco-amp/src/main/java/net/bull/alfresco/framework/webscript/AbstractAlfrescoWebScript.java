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
package net.bull.alfresco.framework.webscript;

import java.util.HashMap;
import java.util.Map;

import net.bull.alfresco.framework.commun.util.Tools;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.log4j.Logger;
import org.novaforge.forge.plugin.alfresco.exception.ForgeException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Classe mere pour les webscript.
 * 
 * @author Cadet_r
 */
public abstract class AbstractAlfrescoWebScript extends DeclarativeWebScript
{
  public static final String        ERREUR_OBJ     = "error";
  public static final String        ERREUR_MESSAGE = "error_message";
  public static final String        ERREUR_DETAIL  = "error_detail";

  protected Logger                  log            = Logger.getLogger(getClass());

  protected NodeService             nodeService;
  protected SearchService           searchService;
  protected ContentService          contentService;
  protected TransactionService      transactionService;
  protected FileFolderService       fileFolderService;
  protected AuthenticationService   authenticationService;
  protected AuthenticationComponent authenticationComponent;

  protected ServiceRegistry         serviceRegistry;
  protected Map<String, Object>     res;

  protected WebScriptRequest        req;

  protected Repository              repositoryHelper;

  protected Status                  status;

  protected Cache                   cache;

  private String                    name;

  /**
   * Le ticket d'authentification
   */
  private String                    ticket;

  /**
   * Constructeur.
   */
  public AbstractAlfrescoWebScript(final String name)
  {
    super();
    this.name = name;
  }

  /**
   * Constructeur par defaut
   */
  public AbstractAlfrescoWebScript()
  {
    this("DefautControleur");
  }

  @Override
  protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
  {
    log.debug("execution du controleur " + getName());
    res = new HashMap<String, Object>();
    this.req = req;
    this.status = status;
    this.cache = cache;
    try
    {
      ticket = authenticationService.getCurrentTicket();
    }
    catch (final Exception e)
    {
      log.error("Pas de ticket : " + Tools.getMessage(e), e);
    }

    transactionService = serviceRegistry.getTransactionService();
    try
    {
      initFromRequest();
      internalTraitement();
      res.put("ticket", getTicket());
    }
    catch (final Exception e)
    {
      log.error("erreur dans le controleur " + getName(), e);
      if (e instanceof ForgeException)
      {
        res.put(ERREUR_MESSAGE, e.getMessage());
        res.put(ERREUR_DETAIL, Tools.getMessage(e));
        res.put(ERREUR_OBJ, e);
      }
      else
      {
        final ForgeException fe = new ForgeException();
        res.put(ERREUR_MESSAGE, fe.getMessage());
        res.put(ERREUR_DETAIL, Tools.getMessage(fe));
        res.put(ERREUR_OBJ, e);
      }
    }
    return res;
  }

  /**
   * Traitement interne du controleur.
   * 
   * @throws Exception
   */
  protected abstract void internalTraitement() throws Exception;

  protected abstract void initFromRequest() throws Exception;

  /**
   * Donne le ticket d'authentification
   * 
   * @return
   */
  protected String getTicket()
  {
    return ticket;
  }

  // pour l'injection
  public void setContentService(final ContentService contentService)
  {
    this.contentService = contentService;
  }

  public void setNodeService(final NodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public void setSearchService(final SearchService searchService)
  {
    this.searchService = searchService;
  }

  public void setFileFolderService(final FileFolderService fileFolderService)
  {
    this.fileFolderService = fileFolderService;
  }

  public void setAuthenticationService(final AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  public void setServiceRegistry(final ServiceRegistry serviceRegistry)
  {
    this.serviceRegistry = serviceRegistry;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public void setAuthenticationComponent(final AuthenticationComponent authenticationComponent)
  {
    this.authenticationComponent = authenticationComponent;
  }

  /**
   * @param repositoryHelper
   *          the repositoryHelper to set
   */
  public void setRepositoryHelper(final Repository repositoryHelper)
  {
    this.repositoryHelper = repositoryHelper;
  }

}
