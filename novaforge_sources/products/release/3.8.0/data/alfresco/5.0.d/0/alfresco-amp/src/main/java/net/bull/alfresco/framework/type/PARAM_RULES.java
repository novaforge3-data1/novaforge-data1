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

/**
 * Types d'info pour la construction de regle.
 * @author Rouviere_x
 *
 */
public enum PARAM_RULES 
{
	/**
	 * Type de la regle. optionnel
	 */
	TYPE_RULES, 
	/**
	 * Nom de l'action. Obligatoire.
	 */
	ACTION_NAME, 
	/**
	 * Propriete de l'action et condition. optionnel
	 */
	ACTION_PROPS, CONDITION_PROPS, CONDITION_DEF_NAME,
	
	/**
	 * Description et titre obligatoire
	 */
	TITLE_RULE, 
	DESC_RULE, APPLY_TO_CHILDREN, EXECUTE_ASYNCHRONE

}
