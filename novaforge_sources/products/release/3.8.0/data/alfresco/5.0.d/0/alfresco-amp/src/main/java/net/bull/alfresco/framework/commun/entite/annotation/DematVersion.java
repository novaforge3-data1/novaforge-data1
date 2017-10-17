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
package net.bull.alfresco.framework.commun.entite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet de versionner.
 * Cette annotation peut etre utiliser sur different type d'element.
 * Nous l'utiliserons principalement sur les TYPE.
 * 
 * Le numero de version sera du type :
 * majeur.mineur.revision.patch <br/>
 * ou
 * numero <br/>
 * 
 * @author Rouviere_x
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DematVersion 
{

	/**
	 * Donne une description de la version.
	 * @return
	 */
	String numero() default "0.0.0";
	
	/**
	 * Donne le numero de la version majeure
	 * 
	 * @return un entier positif
	 */
	int majeur() default 0;

	/**
	 * Numero de la version mineure
	 * @return un entier positif
	 */
	int mineur() default 0;
	
	/**
	 * Numero de patch
	 * @return un entier positif
	 */
	int revision() default 0;
	
	/**
	 * Le patch peut etre de la forme alpha numerique
	 * @return
	 */
	String patch() default "";  
	
	/**
	 * Format du numero de version
	 * @return
	 */
	String format() default "X.X.XXX";
}
