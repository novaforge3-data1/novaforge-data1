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
 * Methodes utile pour la gestion des Thread
 * 
 * @author Rouviere_x
 * @version 1.0
 */
public class ThreadTools 
{

	/**
	 * Fait dormir le thread courrant pendant i * unit
	 * 
	 * @param i quantite e dormir
	 * @param unite de la quantite e dormir
	 * @throws InterruptedException  erreur pendant le repos.
	 */
	public static void sleep(int i, TimeUnit unit) throws InterruptedException
	{
		Thread.sleep(TimeUnit.MILLISECONDS.convert(i, unit));
	}

	/**
	 * idem sleep mais ne propage pas l'exception interrupted
	 * @param i
	 * @param seconds
	 */
	public static void sleepSafe(int i, TimeUnit unit)
	{
		try
		{
			sleep(i, unit);
		}
		catch (InterruptedException e)
		{
		}
		
	}

	/**
	 * Retourne le nom du Thread Courrant.
	 * @return
	 */
	public static String getName()
	{
		Thread t = Thread.currentThread();		
		return "-["+t.getId()+"]:"+t.getName()+"-";
	}
	
	
}
