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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.webscript.AbstractAlfrescoWebScript;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.novaforge.forge.plugin.alfresco.exception.ForgeException;

public class CreateUser extends AbstractAlfrescoWebScript
{
  private String                       login;
  private String                       firstName;
  private String                       lastName;
  private String                       mail;
  private String                       password;
  // private String language;

  private final Logger                 log = Logger.getLogger(this.getClass());
  private PersonService                personService;
  private NodeService                  nodeService;
  private SearchService                searchService;
  private MutableAuthenticationService authenticationService;

  @Override
  protected void initFromRequest() throws Exception
  {
    this.login = this.req.getParameter("login");
    this.firstName = this.req.getParameter("firstName");
    this.lastName = this.req.getParameter("lastName");
    this.mail = this.req.getParameter("mail");
    this.password = this.req.getParameter("password");
    // Should be used for profil
    // language = req.getParameter("language");

    // Initializing services
    this.personService = this.serviceRegistry.getPersonService();
    this.nodeService = this.serviceRegistry.getNodeService();
    this.authenticationService = this.serviceRegistry.getAuthenticationService();

    this.checking();
  }

  /**
   * Checking parameters
   * 
   * @throws ForgeException
   */
  private void checking() throws ForgeException
  {
    boolean isValid = true;
    isValid = isValid && Tools.isSet(this.login);
    isValid = isValid && Tools.isSet(this.firstName);
    isValid = isValid && Tools.isSet(this.lastName);
    isValid = isValid && Tools.isSet(this.mail);
    isValid = isValid && Tools.isSet(this.password);
    // Should be used for profil
    // isValid = isValid && Tools.isSet(language);
    if (!isValid)
    {
      throw new ForgeException(ForgeException.ERR_PARAM_FORGE);
    }
  }

  @Override
  protected void internalTraitement() throws Exception
  {
    this.log.debug("CreateUser()->Start");

    final String userName = this.createUser();
    this.res.put("userName", userName);
    this.res.put("message", "OK");

    this.log.debug("CreateUser()->End");
  }

  /**
   * Create a new user
   * 
   * @return NodeRef
   * @throws Exception
   */
  private String createUser() throws Exception
  {
    String userName = "";
    final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

    // Check if the user already exists
    if (!this.personService.personExists(this.login))
    {
      properties.put(ContentModel.PROP_USERNAME, this.login);
      properties.put(ContentModel.PROP_FIRSTNAME, this.firstName);
      properties.put(ContentModel.PROP_LASTNAME, this.lastName);
      properties.put(ContentModel.PROP_EMAIL, this.mail);
      properties.put(ContentModel.PROP_PASSWORD, this.password);
      properties.put(ContentModel.PROP_HOMEFOLDER, repositoryHelper.getCompanyHome());

      final NodeRef user = this.personService.createPerson(properties);

      userName = (String) this.nodeService.getProperty(user, ContentModel.PROP_USERNAME);
      this.authenticationService.createAuthentication(userName, this.password.toCharArray());
      this.log.debug("authentification possible pour (" + userName + ") ? "
          + this.authenticationService.authenticationExists(userName));

    }
    else
    {
      final NodeRef user = this.personService.getPerson(this.login);
      userName = (String) this.nodeService.getProperty(user, ContentModel.PROP_USERNAME);
    }

    return userName;
  }
}
