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
package net.bull.alfresco.framework.builder;

import java.io.Serializable;
import java.util.Map;

import net.bull.alfresco.framework.type.IDirectoryBuilder;
import net.bull.alfresco.framework.type.ISearchNode;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;


/**
 * Classe utile pour construire des répertoires.
 * 
 * @author Rouviere_x
 *
 */
public class DirectoryBuilder extends BuilderHelper implements IDirectoryBuilder
{

	private NodeService nodeService;

	private ISearchNode searchNode;
	private FileFolderService fileFolderService;
	
	/**
	 * Constructeur de la classe
	 * @param nodeService
	 */
	public DirectoryBuilder()
	{
	}



	/**
	 * Construction d'un répertoire.
	 * Ajoute l'aspect passé en paramètre au noeud créer.
	 * 
	 * @param baseNode
	 * @param nomRep
	 * @param aspect
	 * @param props
	 * @return
	 */
	@Override
	public NodeRef createDir(NodeRef baseNode, QName nomRep, QName aspect, Map<QName, Serializable> props)
	{

		NodeRef node = searchNode.findDirectory(baseNode, nomRep);
        if (node == null)
        {
        	log.debug("Noeud "+nomRep+" inexistant"); 
        	FileInfo info = fileFolderService.create(baseNode, nomRep.getLocalName(), ContentModel.TYPE_FOLDER);
        	node = info.getNodeRef();
        }
        else
        {
        	log.debug("Noeud "+nomRep+" existe déjà.");
        }
        if (aspect != null)
        {
        	log.debug("Ajout de l'aspect "+aspect+ " sur "+nomRep); 
        	nodeService.addAspect(node, aspect, props);
        }		
		return node;
	}

	@Override
	public NodeRef createDir( QName nomRep, QName aspect, Map<QName, Serializable> props)
	{		
		NodeRef baseNode =searchNode.findDirectory(QName.createQName("Company Home"));
		return createDir(baseNode,  nomRep,  aspect,  props);
	}
	/**
	 * Pour avoir notre outils de recherche
	 * @return
	 */
	@Override
	public ISearchNode getSearchNode()
	{
		return searchNode;
	}
	
	@Override
	public void setSearchNode(ISearchNode searchNode)
	{
		this.searchNode = searchNode;
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
	
	
	@Override
	public FileFolderService getFileFolderService()
	{
		return fileFolderService;
	}

	@Override
	public void setFileFolderService(FileFolderService fileFolderService)
	{
		this.fileFolderService = fileFolderService;
	}
}
