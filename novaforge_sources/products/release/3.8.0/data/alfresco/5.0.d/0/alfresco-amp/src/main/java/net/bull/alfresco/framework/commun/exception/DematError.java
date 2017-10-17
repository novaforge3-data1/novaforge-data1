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
package net.bull.alfresco.framework.commun.exception;


/**
 * Erreur grave de l'application. 
 * 
 * 
 * @version 1.0
 */

@SuppressWarnings("serial")
public class DematError extends Error
{

	/**
	 * Constructeur par defaut
	 */
	public DematError()
	{
		super();
	}

	/**
	 * Constructeur avec message d'erreur
	 * 
	 * @param : String message d'erreur
	 */
	public DematError(String message)
	{
		super(message);
	}

	/**
	 * Constructeur avec message d'erreur et exception ayant genere l'erreur
	 * 
	 * @param : String message d'erreur
	 */
	public DematError(String message, Throwable cause)
	{
		super(message,cause);
	}


}