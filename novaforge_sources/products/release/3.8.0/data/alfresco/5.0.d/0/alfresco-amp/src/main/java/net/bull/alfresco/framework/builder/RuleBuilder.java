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
package net.bull.alfresco.framework.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bull.alfresco.framework.type.IRuleBuilder;
import net.bull.alfresco.framework.type.PARAM_RULES;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;


/**
 * Aide à la creation des rules.
 * Yessss.
 * Attention cet objet ne doit pas erte partager par plusieurs Thread.
 * 
 * @author Rouviere_x
 *
 */
public class RuleBuilder extends BuilderHelper implements IRuleBuilder
{

	private RuleService ruleService;
	private ActionService actionService;
	
	/**
	 * Parametres
	 */
	private Map<PARAM_RULES, Serializable> infoRule;

	/**
	 * Constructeur
	 */
	public RuleBuilder()
	{
	}
	
	/**
	 * Construction d'une regle et ajout a un noeud.
	 * Lecture des infos de constructions dans info rules
	 */
	@Override
	public synchronized void addRule(NodeRef node, Map<PARAM_RULES, Serializable> infoRule)
	{
		this.infoRule = infoRule;
		
		Rule r = createRule();
		log.debug("Creation de la regle "+r);
		if (!existRule(ruleService.getRules(node), r))
		{
			log.debug("Association de la regle "+r+" au noeud "+node);
			ruleService.saveRule(node, r);
		}
		else
		{
			log.debug("La regle "+r+" existe deja sur le noeud : "+node);
		}
	}
	

	/**
	 * Test de l'existance d'une regle sur un noeuds.
	 * 
	 * @param rules
	 * @param r
	 * @return
	 */
	public boolean existRule(List<Rule> rules, Rule rule)
	{
		boolean sameTitre = false, sameType=false, sameAction=false;
		boolean res = false;
		for(Rule r : rules)
		{		
			sameTitre = r.getTitle().equals(rule.getTitle());
			sameAction = r.getAction().getActionDefinitionName().equals(rule.getAction().getActionDefinitionName());
			sameType = isSameType(r, rule);
			res = sameTitre && sameType && sameAction;
			if (res)
				return true;
		}
		return false;
	}

	/**
	 * Indique si les regles A et B sont de meme type.
	 * 
	 * @param r
	 * @param rule
	 * @return
	 */
	public boolean isSameType(Rule ruleA, Rule ruleB)
	{		
		List<String> lstA = ruleA.getRuleTypes();
		List<String> lstB = ruleB.getRuleTypes();
		if (lstA.size() != lstB.size())
			return false;
		
		for(String tA : lstA)
		{
			if (!lstB.contains(tA))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Creation de la regle
	 * @return
	 */
	private Rule createRule()
	{
        List<String> ruleTypes = new ArrayList<String>(1);
        Object val = infoRule.get(PARAM_RULES.TYPE_RULES);
		RuleType ruleType = this.ruleService.getRuleType(val == null ? RuleType.INBOUND : (String)val);
        ruleTypes.add(ruleType.getName());
        
        // Creation de l'action
        Action action = this.actionService.createAction((String)infoRule.get(PARAM_RULES.ACTION_NAME));
        
        Map<String, Serializable> actionProps = (Map<String, Serializable>) infoRule.get(PARAM_RULES.ACTION_PROPS);
        if (actionProps != null)
        {
        	action.setParameterValues(actionProps);
        }
        Map<String, Serializable> conditionProps = (Map<String, Serializable>) infoRule.get(PARAM_RULES.CONDITION_PROPS);
        if (conditionProps != null)
        {
        	ActionCondition actionCondition = this.actionService.createActionCondition((String)infoRule.get(PARAM_RULES.CONDITION_DEF_NAME));
        	actionCondition.setParameterValues(conditionProps);
        	action.addActionCondition(actionCondition);
        }
        
        // Create the rule
        Rule rule = new Rule();
        rule.setRuleTypes(ruleTypes);
		rule.setDescription((String)infoRule.get(PARAM_RULES.DESC_RULE));
		rule.setTitle((String)infoRule.get(PARAM_RULES.TITLE_RULE));
		val = infoRule.get(PARAM_RULES.APPLY_TO_CHILDREN);
		
        rule.applyToChildren((val == null ? false : (Boolean)val));        
        rule.setAction(action);
        val = infoRule.get(PARAM_RULES.EXECUTE_ASYNCHRONE);
        rule.setExecuteAsynchronously((val == null ? false : (Boolean)val));
        return rule;
	}
	
	@Override
	public RuleService getRuleService()
	{
		return ruleService;
	}

	@Override
	public void setRuleService(RuleService ruleService)
	{
		this.ruleService = ruleService;
	}

	@Override
	public ActionService getActionService()
	{
		return actionService;
	}

	@Override
	public void setActionService(ActionService actionService)
	{
		this.actionService = actionService;
	}



	
}
