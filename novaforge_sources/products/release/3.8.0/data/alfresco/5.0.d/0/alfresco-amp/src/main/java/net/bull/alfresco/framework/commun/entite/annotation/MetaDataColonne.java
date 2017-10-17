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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.bull.alfresco.framework.commun.entite.type.FORMAT;

/**
 * Information sur une colonne.
 * Donne le type,la position de la colonne, et une description de la colonne
 * 
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetaDataColonne 
{

	/**
	 * Donne le format des colonnes de la liste
	 * La taille du tableau devrait etre egale a la valeur du count
	 */
	FORMAT format() default FORMAT.CHAR;
	
	/**
	 * Donne la position de la colonne dans la liste.
	 */
	int pos() default 0;
	
	/**
	 * Donne une description de la colonne 
	 */
	String desc() default ".: COL :.";
	
	/**
	 * Donne une description de la colonne 
	 */
	String name() default "COL";
}
