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
package net.bull.alfresco.framework.utils;

import java.io.Serializable;
import java.util.Iterator;

import net.bull.alfresco.framework.commun.util.Tools;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Ensemble de methode pour faciliter la création de requete Lucene
 * @author xrouviere
 *
 */
public abstract class LuceneUtils
{

	static Logger log = Logger.getLogger(LuceneUtils.class);
	
	/**
	 * Cr�ation d'une requete lucienne pour recuperer les noeuds
	 * @param type TYPE ou ASPECT ou EXACTTYPE...
	 * @param valType = 
	 * @param nom nom du noeud
	 * @return
	 */
	public static String createQueryType(String type, QName valType)
	{
		String query = type+":\""+valType+"\"";
		log.debug("createLuceneQueryType()-->"+query);
		return query;	
	}
	
	/**
	 * Cr�ation d'un critere avec le nom
	 * @param nom
	 * @return
	 */
	public static String createQueryName(QName nom)
	{
		String query = "@cm\\:name:\""+nom.getLocalName()+"\"";
		return query;
	}
	
	/**
	 * Création d'un critere pour la propriete propriete.
	 * La propriete doitêtre de type string.
	 * Le crietre est fait avec une valeur exacte.
	 * 
	 * @param nom
	 * @return
	 */ 
	public static String createQueryStringExactVal(QName propriete, Serializable valeur)
	{		
		String p = propriete.getPrefixString().replaceAll(":", "\\\\:");
		String query = "@"+p+":\""+valeur+"\"";
		return query;
	}

	/**
	 * Moins pire que la précédente mais pas top non plus.
	 * Il est préférable d'utiliser   {@link #createQueryStringExactVal(QName, Serializable, NamespacePrefixResolver)} � la place.
	 * @param prefixe
	 * @param propriete
	 * @param valeur
	 * @return
	 */
	public static String createQueryStringExactVal(String prefixe, QName propriete, Serializable valeur)
	{	
		String query = "@"+prefixe+"\\:"+propriete.getLocalName()+":\""+valeur+"\"";
		return query;
	}
	
	/**
	 * M�thode � utiliser pour etre sur.
	 * @param prefixe
	 * @param propriete
	 * @param valeur
	 * @param resolver 
	 * @return
	 */
	public static String createQueryStringExactVal(QName propriete, Serializable valeur, NamespacePrefixResolver resolver)
	{
		String query = "@"+getQueryName(propriete, resolver)+":\""+valeur+"\"";
		return query;
	}
	/**
	 * Cr�ation d'une requete lucenne de type path avec 
	 * 
	 * @param p
	 * @param resolver 
	 * @return
	 */
	public static String createQueryPath(Path p, NamespacePrefixResolver resolver)
	{
		Iterator<Element> elems = p.iterator();
		String res = new String("PATH:\"");
		boolean hasElem = elems.hasNext(); // juste pour savoir si je met un / � la fin
		while (elems.hasNext())
		{
			res = res.concat(elems.next().getPrefixedString(resolver));
			res = res.concat("/");
		}
		if (!hasElem)
			res +="/";
		res = res.concat("/*\"");
		

		return res;
	}
	
	/**
	 * Cr�ation d'un critere boolean
	 * @param propIsmodulevisible
	 * @param b
	 */
	public static String createQueryBoolean(QName propName, boolean valeur, NamespacePrefixResolver resolver)
	{
		String query = "@"+getQueryName(propName, resolver)+":"+valeur;
		return query;		
	}

	/**
	 * Renvoie le nom de la propriete pour faire une requete Lucenne
	 * @param propName
	 * @param resolver
	 * @return
	 */
	public static String getQueryName(QName propName, NamespacePrefixResolver resolver)
	{
		return propName.toPrefixString(resolver).replace(":", "\\:");
	}

	/**
	 * Ajout la requete query � la requete dans les criteres de recherche.
	 * @param critere
	 * @param query
	 */
	public static void andCritere(SearchParameters critere, String query)
	{
		assert (critere != null);
		assert (query != null);
		String queryOrigine = critere.getQuery(); // il y a d�j� une requete ?
		// il faudrait v�rifier que c'est bien une requete de type lucienne.
		if (Tools.isSet(queryOrigine))
		{// oui il y a une requete concatenons le chemin
			query = query.concat(" AND ").concat(queryOrigine);
		}
		critere.setQuery(query);
	}

	/**
	 * Cr�ation d'un objet critere de base pour faire de la requete ma lucienne
	 * @return
	 */
	public static SearchParameters baseCritere()
	{
		SearchParameters critSearch = new SearchParameters();
		critSearch.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		critSearch.setLanguage(SearchService.LANGUAGE_LUCENE);
		return critSearch;
	}


}
