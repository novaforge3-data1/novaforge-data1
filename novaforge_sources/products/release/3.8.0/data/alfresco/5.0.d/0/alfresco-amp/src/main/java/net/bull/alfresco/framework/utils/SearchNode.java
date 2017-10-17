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

import static net.bull.alfresco.framework.utils.AlfrescoFormatTools.nodeStr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.bull.alfresco.framework.type.ISearchNode;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 * Pour essayer de trouver son bonheur dans le sac de noeuds.
 * 
 * @author Rouviere_x
 *
 */
public class SearchNode implements ISearchNode
{

	private StoreRef store = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
	
	private NodeService nodeService;
	
	protected Logger log = Logger.getLogger(getClass());
	
	/**
	 * Constructeur pour avoir un chercheur.
	 * 
	 * @param nodeService
	 */
	public SearchNode(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Pour un creation automatique.
	 */
	public SearchNode()
	{		
	}

	/**
	 * Recherche d'un noeud de type repertoire de nom 
	 * 
	 * @param root repertoire de depart
	 * @param name nom du noeud.
	 * 
	 * @return un noeud ou null.
	 */
	public NodeRef findDirectory(NodeRef root, String name)
	{	
		return findContent(root, name, ContentModel.TYPE_FOLDER);
	}
	
	public NodeRef findDirectory(NodeRef root, QName name)
	{
		return findDirectory(root, name.getLocalName());
	}
	
	public NodeRef findDirectory(String nomRep)
	{
		NodeRef root = nodeService.getRootNode(store);
		return findDirectory(root, nomRep);
	}
	
	public NodeRef findDirectory(QName nomRep)
	{		
		return findDirectory( nomRep.getLocalName());
	}
	
	/**
	 * Recherche d'un noeud e partir du root.
	 * Niveau d'exploration 3.
	 * 
	 * @param root
	 * @param localName
	 * @param typeFolder
	 * @return
	 */
	public NodeRef findContent(NodeRef root, String localName, QName type)
	{
		return recurseSearch(root, localName, type, 3, 0);
	}

	/**
	 * Recherche type exact nom exact sur max niveau.
	 * 
	 * @param root
	 * @param localName
	 * @param type
	 * @param i
	 * @return
	 */
	protected  NodeRef recurseSearch(NodeRef root, String localName, QName type, int max, int current )
	{
		log.trace("("+current+")recurseSearch("+nodeStr(root,nodeService)+","+localName+","+type.getLocalName()+","+max+","+current+")->Debut");
		NodeRef result;
		List<ChildAssociationRef> lst = nodeService.getChildAssocs(root);
		List<NodeRef> nextLevel = new ArrayList<NodeRef>(lst.size());
		NodeRef child;
		for(ChildAssociationRef car : lst)
		{// je parcours les fils
			child = car.getChildRef();
			
			if (nodeService.getType(child).isMatch(type))
			{// le type corresponds
				Serializable name = nodeService.getProperty(child, ContentModel.PROP_NAME);
				if (name.equals(localName))
				{// le nom correspond... j'ai le premier yes....je sort
					log.trace("("+current+")recurseSearch("+nodeStr(root,nodeService)+","+localName+","+type.getLocalName()+","+max+","+current+")->Fin Direct : "+child);
					return child;
				}
			}
			//c'est pas le bon. est-ce que c'est un repertoire ?
			// et est-ce que je doit explorer
			if (nodeService.getType(child).isMatch(ContentModel.TYPE_FOLDER) && current < max)
			{// oui. je garde ce noeud et je continue pour voir si il n'est pas plus loin dans ma liste.
				nextLevel.add(child);
			}
		}
		// bon pas de chance il n'est pas dans ce noeud.
		if (nextLevel.size() > 0)
		{// j'ai d'autre piste
			for(NodeRef piste : nextLevel)
			{// boum recherche exponnentielle => on limite e 3 niveaux
				result = recurseSearch(piste,localName,type,max,current+1);
				if (result != null)
				{// yes on l'a
					log.trace("("+current+")recurseSearch("+nodeStr(root,nodeService)+","+localName+","+type.getLocalName()+","+max+","+current+")->Fin ("+current+")"+result);
					return result;
				}
			}
		}
		log.debug("("+current+")recurseSearch("+nodeStr(root,nodeService)+","+localName+","+type.getLocalName()+","+max+","+current+")->Fin {}");
		return null;
	}

	
	public void setStore(StoreRef store)
	{
		this.store = store;
	}

	public StoreRef getStore()
	{
		return store;
	}

	@Override
	public NodeService getNodeService()
	{
		return nodeService;
	}
	
	@Override
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}

	/**
	 * Recherche du noeud de nom name dans la liste.
	 * @param lst
	 * @param nameDepotCsv
	 * @param nodeService
	 * 
	 * @return NodeRef
	 */
	public NodeRef findNode(List<NodeRef> lst, QName name)
	{
		NodeRef resultat = null;
		String nameL = name.getLocalName();
		String nodeName;
		for(NodeRef node : lst)
		{
			nodeName =getName(node);
			if (nameL.equals(nodeName))
			{
				return node;
			}			
		}		
		return resultat;
	}

	/**
	 * Donne le nom du noeud.
	 * Lecture de la propriete  ContentModel.PROP_NAME.
	 * @param node
	 * @return
	 */
	public String getName(NodeRef node)
	{
		return (String)nodeService.getProperty(node,ContentModel.PROP_NAME);
	}


}
