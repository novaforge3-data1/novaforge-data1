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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Classe pour le formatage des donnees.
 * 
 * @author Rouviere_x
 *
 */
public class AlfrescoFormatTools
{

	/**
	 * Fournit une description simple d'un noeuds
	 * @param node
	 * @return
	 */
	public static String nodeStr(NodeRef node, NodeService nodeService)
	{
		StringBuffer val = new StringBuffer("{");
		if (node != null)
		{
			val.append(node.getStoreRef().toString()).append("/");
			val.append(nodeService.getProperty(node, ContentModel.PROP_NAME));
			
		}
		val.append("}");
		return val.toString();
	}

	/**
	 * Fournit une description simple d'un noeud.
	 * 
	 * @param node
	 * @return
	 */
	public static String actionStr(Action action)
	{
		StringBuffer val = new StringBuffer("{");
		if (action != null)
		{
			val.append(action.getId());
		}
		val.append("}");
		return val.toString();
	}
	
	/**
	 * Donne le nom du noeud.
	 * Lecture de la propriete  ContentModel.PROP_NAME.
	 * @param node
	 * @return
	 */
	public static String getName(NodeService nodeService, NodeRef node)
	{
		return (String)nodeService.getProperty(node,ContentModel.PROP_NAME);
	}
	

	public static final String SEPARATEUR = "_";

	/**
	 * Donne le nom d'un dossier formatte avec la date du jour<br/>
	 * 
	 * @return
	 */
	public static String getFormatDateDirName(Date dateRef)
	{
		
		String format = "dd"+SEPARATEUR+"MM"+SEPARATEUR+"yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
    	String formatedfolderName = dateFormat.format(dateRef);
    	
    	return formatedfolderName;
	}

	/**
	 * Creation du nom d'un repertoire en remplaeant les espaces et les '
	 * @param nomOrigine
	 * @return
	 */
	public static String createNomFolder(String nomOrigine)
	{
		String nomAgent = nomOrigine.replaceAll(" ","_");
		nomAgent = nomAgent.replaceAll("'","_");
		return nomAgent;
	}
}
