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
package net.bull.alfresco.framework.type;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Type de constructeur de repertoire.
 * Surcouche sur le nodeService et sur le fileFolderService.
 * 
 * @author Rouviere_x
 *
 */
public interface IDirectoryBuilder
{

	/**
	 * Creation d'un repertoire  de type {@link ContentModel#TYPE_FOLDER}
	 * 
	 * @param baseNode noeud de depart
	 * @param nomRep nom du repertoire
	 * @param aspect aspect du repertoire
	 * @param props proprietes de l'aspect
	 * @return
	 */
	public NodeRef createDir(NodeRef baseNode, QName nomRep, QName aspect, Map<QName, Serializable> props);

	/**
	 * Creation d'un repertoire dans Company home
	 * @param nomRep
	 * @param aspect
	 * @param props
	 */
	public NodeRef createDir(QName nomRep, QName aspect, Map<QName, Serializable> props);
	/**
	 * Gestion du searchNode
	 * @return
	 */
	public ISearchNode getSearchNode();

	public void setSearchNode(ISearchNode searchNode);
	

	/**
	 * Gestion du fileFolderService
	 * @return
	 */
	public FileFolderService getFileFolderService();

	public  void setFileFolderService(FileFolderService fileFolderService);

	/**
	 * Gestion du node Service 
	 * @return
	 */
	public NodeService getNodeService();

	public void setNodeService(NodeService nodeService);

	

}
