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
package net.bull.alfresco.framework.commun.entite.type;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface definissant un contexte de travail.
 * Le contexte de travail contient des informations relative e l'utilisateur qui se connecte.
 * Ce contexte sera commun cote Application et cote Alfesco.
 * Une serialisation en objet JSon du contexte sera systematiquement passe en parametre au webscript.
 * 
 * Attention il ne faut pas mettre Des informations en plus dans ce contexte (sauf si c'est commun)
 * Pour un parametrage specifique il suffit d'utiliser les cles/valeurs.
 * 
 *  
 * @author Rouviere_x
 *
 */
public interface ContexteTravail
{
	public static String MODULE_ID = "module_id";
	
	public static String COLLECTIVITE_ID = "collectivite_id";
	public static String TICKET_ID = "ticket";

	/**
	 * Information utilisateur
	 */
	public static String USER_ID = "user_id";
	public static String USER_NOM = "user_nom";
	public static String USER_PRENOM = "user_prenom";
	public static String USER_MAIL = "user_mail";
	
	/**
	 * Fournit l'identifiant de l'utilisateur.
	 * Obligatoire.
	 * @return identifiant de l'utilisateur.
	 */
	public String getUserId();
	
	/**
	 * Fournit le nom de l'utilisateur
	 * Obligatoire
	 * @return le nom de l'utilisateur
	 */
	public String getUserNom();
	
	/**
	 * Fournit le prenom de l'utilisateur
	 * Obligatoire
	 * @return le prenom de l'utilisateur
	 */
	public String getUserPrenom();
	
	/**
	 * Fournit l'adresse mail de l'utilisateur
	 * peut etre null
	 * @return
	 */
	public String getUserMail();
	
	/**
	 * Fournit l'identifiant d'une collectivte.
	 * @return organisme courant ou null
	 */
	public String getCollectiviteId();

	
	/**
	 * Fournit le module courrant.
	 * Peut etre null.
	 * @return module courant ou null
	 */
	public String getModuleId();
	
	/**
	 * Fournit le ticket d'authentification du contexte en cours..
	 * @return organisme courant ou null
	 */
	public String getTicketId();
	
	/**
	 * Positionne un valeur dans le contexte de travail.
	 * Valeur nulle deconseille.
	 * 
	 * @param key
	 * @param val
	 */
	public void setParamContexte(String key, Serializable val);
	
	/**
	 * Recupere une valeur dans le contexte de travail.
	 * Renvoie null si le parametre n'existe pas.
	 * @param key
	 * @return
	 */
	public Serializable getParamContexte(String key);
	
	/**
	 * Renvoie l'ensemble des parametres.
	 * @return
	 */
	public Map<String, Serializable> getContexte();
}
