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

import java.util.concurrent.TimeUnit;

/**
 * Methode pour la manipulation des dates.<br/>
 * 
 * @author Rouviere_x
 * @version 1.0
 */
public class TimeTools
{
	/**
	 * Valeur d'une seconde en milliseconde
	 */
	public static final long SECONDE = 1000L;
	/**
	 * Valeur d'une minute en milliseconde
	 */
	public static final long MINUTE = 60*SECONDE;
	/**
	 * Valeur d'une heure en milliseconde
	 */
	public static final long HEURE = 60*MINUTE;
	/**
	 * Valeur d'un jour en milliseconde
	 */	
	public static final long JOUR = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
	
	
	/**
	 * Renvoie la duree en milliseconde de qte
	 * 
	 * @param qte
	 * @param unit
	 * @return conversion en milliseconde d'une valeur 
	 */
	public static long getMillis(long qte, TimeUnit unit)
	{
		long res = 0L;
		res=TimeUnit.MILLISECONDS.convert(qte, unit);
		return res;
	}
}
