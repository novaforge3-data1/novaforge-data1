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

public class UpdateUser extends AbstractAlfrescoWebScript
{
  private String                       login;
  private String                       firstName;
  private String                       lastName;
  private String                       mail;
  private String                       password;
  private String                       language;

  private final Logger                 log = Logger.getLogger(getClass());
  private PersonService                personService;
  private NodeService                  nodeService;
  private SearchService                searchService;
  private MutableAuthenticationService authenticationService;

  @Override
  protected void initFromRequest() throws Exception
  {
    login = req.getParameter("login");
    firstName = req.getParameter("firstName");
    lastName = req.getParameter("lastName");
    mail = req.getParameter("mail");
    password = req.getParameter("password");
    language = req.getParameter("language");

    checking();
  }

  /**
   * Checking parameters
   * 
   * @throws ForgeException
   */
  private void checking() throws ForgeException
  {
    boolean isValid = true;
    isValid = isValid && Tools.isSet(login);
    isValid = isValid && Tools.isSet(firstName);
    isValid = isValid && Tools.isSet(lastName);
    isValid = isValid && Tools.isSet(mail);
    isValid = isValid && Tools.isSet(password);
    isValid = isValid && Tools.isSet(language);
    if (!isValid)
    {
      throw new ForgeException(ForgeException.ERR_PARAM_FORGE);
    }
  }

  @Override
  protected void internalTraitement() throws Exception
  {
    log.debug("UpdateUser()->Start");

    final String userName = updateUser();
    res.put("userName", userName);
    res.put("message", "OK");

    log.debug("UpdateUser()->End");
  }

  /**
   * Create a new user
   * 
   * @return NodeRef
   * @throws Exception
   */
  private String updateUser() throws Exception
  {
    String userName = "";
    personService = serviceRegistry.getPersonService();
    nodeService = serviceRegistry.getNodeService();
    authenticationService = serviceRegistry.getAuthenticationService();
    final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

    // Check if the user already exists
    if (personService.personExists(login))
    {
      properties.put(ContentModel.PROP_USERNAME, login);
      properties.put(ContentModel.PROP_FIRSTNAME, firstName);
      properties.put(ContentModel.PROP_LASTNAME, lastName);
      properties.put(ContentModel.PROP_EMAIL, mail);
      properties.put(ContentModel.PROP_PASSWORD, password);
      properties.put(ContentModel.PROP_HOMEFOLDER, repositoryHelper.getCompanyHome());

      final NodeRef user = personService.getPerson(login);

      userName = (String) nodeService.getProperty(user, ContentModel.PROP_USERNAME);

      // MAJ des propriétés
      personService.setPersonProperties(userName, properties);
      authenticationService.setAuthentication(userName, password.toCharArray());
      log.debug("authentification possible pour (" + userName + ") ? "
          + authenticationService.authenticationExists(userName));

    }
    else
    {
      throw new ForgeException(ForgeException.ERR_USER_NOT_EXIST);
    }

    return userName;
  }

}
