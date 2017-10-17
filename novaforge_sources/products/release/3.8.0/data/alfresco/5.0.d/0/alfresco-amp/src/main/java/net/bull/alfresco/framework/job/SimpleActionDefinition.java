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
package net.bull.alfresco.framework.job;

import org.alfresco.repo.action.scheduled.SimpleTemplateActionDefinition;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Action simple avec un nom.
 * Pour des actions sans parametre.
 * 
 * @see  SimpleTemplateActionDefinition
 * @author Rouviere_x
 *
 */
public class SimpleActionDefinition extends AbstractTemplateAction
{

	private String actionName;
	
	/**
	 * Constrcuteur par defaut.
	 */
	public SimpleActionDefinition()
	{
		super();
	}
	
	
	@Override
	public Action getAction(NodeRef nodeRef)
	{
        // Build the base action
        Action action = actionService.createAction(getActionName());
        log.debug("recuperation de l'action "+getActionName() +" : "+action);
		return action;
	}
	
	
	public String getActionName()
	{
		return actionName;
	}

	public void setActionName(String actionName)
	{
		this.actionName = actionName;
	}



}
