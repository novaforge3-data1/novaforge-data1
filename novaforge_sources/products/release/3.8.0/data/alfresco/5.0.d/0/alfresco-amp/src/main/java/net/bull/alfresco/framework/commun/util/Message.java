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
package net.bull.alfresco.framework.commun.util;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Classe pour la gestion des messages.
 * 
 * @author Rouviere_x
 *
 */
public class Message extends ResourceBundleMessageSource
{

	/**
	 * Construit un fichier de message.
	 * @param basename
	 * @param c 
	 */
	public Message(String basename)
	{
		super();
		this.setBundleClassLoader(new MessageLoader());
		this.setBasename(basename);
		this.setUseCodeAsDefaultMessage(true);
	}
	
	/**
	 * Construit un fichier de message avec un nom de classe
	 * @param c
	 */
	public Message(Class<?> c)
	{
		this(c.getName());
	}

	/**
	 * Retourne le message associe au code
	 * @param code
	 * @return
	 */
	public String getMessage(String code)
	{
		return this.getMessage(code, null);
	}

	/**
	 * Retourne le message associee 
	 * @param code
	 * @param param
	 * @return
	 */
	public String getMessage(String code, Object[] param)
	{
		return  super.getMessage(code, param, getDefaultMessage(code)+ " NON DEFINI", Locale.FRANCE);
	}
}
