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
package net.bull.alfresco.framework.type;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleService;

/**
 * Type de constructeur de regles
 * @author Rouviere_x
 *
 */
public interface IRuleBuilder
{

	/**
	 * Methode pour la construction d'une regle.
	 * Construit la regle.
	 * l'ajoute au noeud.
	 * @param node
	 * @param infoRule
	 */
	public void addRule(NodeRef node, Map<PARAM_RULES, Serializable> infoRule);
	
	/**
	 * Gestion du rule Service
	 * @return
	 */
	public RuleService getRuleService();

	public void setRuleService(RuleService ruleService);

	/**
	 * Gestion de l'action service.
	 * @return
	 */
	public ActionService getActionService();

	public void setActionService(ActionService actionService);

	
	

}
