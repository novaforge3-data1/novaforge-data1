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
package net.bull.alfresco.framework.commun.entite.object;

import net.bull.alfresco.framework.commun.entite.type.DematItem;
import net.bull.alfresco.framework.commun.entite.type.FORMAT;


/**
 * Meta donnee sur une liste.
 * 
 * @author Rouviere_x
 *
 */
public interface InfoListe
{
	/**
	 * Nombre de colonne
	 * @param elem
	 * @return
	 */
	public int getNbColonne();
	
	/**
	 * Renvoie le format de la colonne index
	 * @param elem
	 * @param index
	 * @return
	 */
	public FORMAT getFormat(int index);
	
	/**
	 * Renvoie la description de la colonne index
	 * @param elem
	 * @param index
	 * @return
	 */
	public String getDescription(int index);
	
	/**
	 * Renvoie le nom de la colonne index
	 * @param elem
	 * @param index
	 * @return
	 */
	public String getNom(int index);
	
	/**
	 * Renvoie le nom de la methode
	 * @param elem
	 * @param index
	 * @return
	 */
	public String getNomMethod(int index);
	
	/**
	 * Donne une description de la liste
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Renvoie la valeur de la colonne index de l'objet item passe en parametre.
	 * Renvoie null si l'index de colonne ne correspond pas
	 * Renvoie la valeur formate en fonction du type de la colonne
	 * 
	 * 
	 * @param item valeur e renvoyer.
	 * @param index index de la colonne
	 * @return Une representation String de la valeur 
	 */
	public String getString(DematItem item, int index);
	
	/**
	 * Renvoie la valeur de la colonne index de l'objet item passe en parametre.
	 * Renvoie null si l'index de colonne ne correspond pas
	 * 
	 * @param item valeur e renvoyer.
	 * @param index index de la colonne
	 * @return Une representation String de la valeur 
	 */	
	public Object getObject(DematItem item, int index);
	
	
}
