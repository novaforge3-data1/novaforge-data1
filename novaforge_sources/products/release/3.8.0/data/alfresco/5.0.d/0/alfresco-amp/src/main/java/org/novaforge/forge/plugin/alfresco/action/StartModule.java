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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import net.bull.alfresco.framework.builder.RuleBuilder;
import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.type.PARAM_RULES;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.module.AbstractModuleComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * Classe de demarrage.
 * Appeller en fonction des propriétés du bean
 * <property name="sinceVersion" value="1.0" />
 * <property name="appliesFromVersion" value="1.0" />
 * Et de la valeur lu dans le module.properties
 * module.version=1.0.0
 * 
 * @author Cadet_r
 */
public class StartModule extends AbstractModuleComponent implements InitializingBean, RunAsWork<String>
{

  private final Logger                 log               = Logger.getLogger(getClass());
  private PersonService                personService;
  private NodeService                  nodeService;
  private SearchService                searchService;
  private AuthorityService             authorityService;
  private MutableAuthenticationService authenticationService;
  private FileFolderService            fileFolderService;
  private RuleBuilder                  ruleBuilder;

  private Repository                   repositoryHelper;

  public static String                 ADMIN_FORGE       = "admin_forge";
  public static String                 ADMIN_FORGE_GROUP = "ALFRESCO_ADMINISTRATORS";
  public static String                 ADMIN_PWD         = "forge";

  /**
   * False par défaut
   */
  public StartModule()
  {
    super();
    setExecuteOnceOnly(false); // execution à chaque fois
  }

  /**
   * Création des répertoires de travail
   */
  @Override
  public String doWork() throws Exception
  {
    // création des répertoires de travail.
    nodeService = serviceRegistry.getNodeService();
    fileFolderService = serviceRegistry.getFileFolderService();
    searchService = serviceRegistry.getSearchService();
    personService = serviceRegistry.getPersonService();
    authorityService = serviceRegistry.getAuthorityService();
    authenticationService = serviceRegistry.getAuthenticationService();

    createGroupAndUser();
    // createModeleProjet();
    createSiteRule();
    return "ok";
  }

  /**
   * Création de l'utilisateur admin_forge
   * 
   * @param searchService
   * @param personService
   * @throws SystemException
   * @throws NotSupportedException
   */
  private void createGroupAndUser() throws NotSupportedException, SystemException
  {
    final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

    // createGroup(null, COLLAB_FORGE);
    // Création du user gestionnaire
    if (!personService.personExists(ADMIN_FORGE))
    {
      properties.put(ContentModel.PROP_USERNAME, ADMIN_FORGE);
      properties.put(ContentModel.PROP_FIRSTNAME, ADMIN_FORGE);
      properties.put(ContentModel.PROP_LASTNAME, ADMIN_FORGE);
      properties.put(ContentModel.PROP_EMAIL, ADMIN_FORGE + "@forge.net");
      properties.put(ContentModel.PROP_PASSWORD, ADMIN_PWD);
      properties.put(ContentModel.PROP_HOMEFOLDER, repositoryHelper.getCompanyHome());
      createUser(properties, ADMIN_FORGE_GROUP);
    }

  }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    // Nothing to do
  }

  /**
   * Appel de la méthode doWork avec l'utilisateur Systeme
   */
  @Override
  protected void executeInternal() throws Throwable
  {
    final String res = AuthenticationUtil.runAs(this, AuthenticationUtil.SYSTEM_USER_NAME);
  }

  /**
   * Création d'un user
   * 
   * @param Map
   *          properties
   * @param TYPE_PROFIL
   *          [] groupe
   * @throws SystemException
   * @throws NotSupportedException
   *           : c'est quand Alfreso ne peut plus te supporter
   */
  private void createUser(final Map<QName, Serializable> properties, final String groupe)
      throws NotSupportedException, SystemException
  {
    try
    {
      final NodeRef user = personService.createPerson(properties);

      final String userName = (String) nodeService.getProperty(user, ContentModel.PROP_USERNAME);
      authorityService.addAuthority(authorityService.getName(AuthorityType.GROUP, groupe), userName);

      authenticationService.createAuthentication(userName, ADMIN_PWD.toCharArray());
    }
    catch (final Exception e)
    {
      log.error("Erreur création de l'utilisateur [" + properties.get(ContentModel.PROP_USERNAME) + "]:["
          + Tools.getMessage(e) + "]");
    }
  }

  /**
   * Création d'un groupe.
   * methode géré dans une transaction.
   * 
   * @param GROUP_NAME
   *          name : le nom du groupe à créer
   * @param String
   *          parentName ! le nom du groupe parent
   * @throws SystemException
   * @throws NotSupportedException
   */
  private String createGroup(final String parentName, final String shortName)
  {
    return createGroup(parentName, shortName, shortName);
  }

  private String createGroup(final String parentName, final String shortName, final String displayName)
  {
    final String name = authorityService.getName(AuthorityType.GROUP, shortName);
    if (!authorityService.authorityExists(name))
    {
      try
      {
        final Set<String> parentGrp = new HashSet<String>();

        parentGrp.add(AuthorityService.ZONE_APP_DEFAULT);
        parentGrp.add(AuthorityService.ZONE_AUTH_ALFRESCO);
        final String cn = authorityService.createAuthority(AuthorityType.GROUP, shortName, displayName,
            parentGrp);
        if (Tools.isSet(parentName))
        {
          authorityService.addAuthority(parentName, cn);
        }
      }
      catch (final Exception e)
      {
        log.error("Erreur création du groupe " + name + " dans " + parentName + " [" + Tools.getMessage(e)
            + "]");
      }
    }
    return name;

  }

  /**
   * Create a rule to populate site project.
   */
  private void createSiteRule()
  {
    final NodeRef company = repositoryHelper.getCompanyHome();
    final Map<PARAM_RULES, Serializable> infoRule = new HashMap<PARAM_RULES, Serializable>();

    infoRule.put(PARAM_RULES.APPLY_TO_CHILDREN, Boolean.TRUE);
    infoRule.put(PARAM_RULES.EXECUTE_ASYNCHRONE, Boolean.FALSE);

    infoRule.put(PARAM_RULES.TITLE_RULE, "Populate Site Project");
    infoRule.put(PARAM_RULES.DESC_RULE, "Populate Site Project");
    infoRule.put(PARAM_RULES.TYPE_RULES, RuleType.INBOUND);
    infoRule.put(PARAM_RULES.ACTION_NAME, "populate-site");

    ruleBuilder.addRule(company, infoRule);
  }

  public void setRuleBuilder(final RuleBuilder ruleBuilder)
  {
    this.ruleBuilder = ruleBuilder;
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
