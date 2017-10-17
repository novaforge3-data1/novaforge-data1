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

import java.util.List;

/**
 * Objet resultat d'une validation.<br/>
 * L'idee est de gerer une liste d'erreur et d'alerte.
 * 
 * @author Rouviere_x
 *
 */
public interface ValidationResultat
{

	/**
	 * Indique si le resultat contient des erreurs
	 * @return
	 */
	public boolean hasErreur();

	/**
	 * Ajoute une erreur.
	 * @param erreur
	 */
	public void addErreur(Throwable erreur);
	
	/**
	 * Renvoie la premiere erreur.
	 * Null si pas d'erreur.
	 * @return
	 */
	public Throwable getErreur();
	
	/**
	 * Renvoie la liste d'erreur
	 * @return
	 */
	public List<Throwable> getErreurs();
	
	/**
	 * Indique si il y a des alertes
	 * @return
	 */
	public boolean hasAlerte();
	
	/**
	 * Renvoie la permiere alerte 
	 * @return
	 */
	public Throwable getAlerte();
	
	/**
	 * Renvoie la liste d'alerte
	 * @return
	 */
	public List<Throwable> getAlertes();

	/**
	 * Ajoute une alerte.
	 * @param erreur
	 */
	public void addAlerte(Throwable erreur);

	/**
	 * Ajoute une liste de verification
	 * @param verifRes
	 */
	public void add(ValidationResultat verifRes);
}
