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

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Outils de recherche de noeuds
 * @author Rouviere_x
 *
 */
public interface ISearchNode
{

	/**
	 * Accesseur du nodeService utiliser pour la recherche.
	 * @return
	 */
	public NodeService getNodeService();

	public void setNodeService(NodeService nodeService);

	/**
	 * Recherche du noeud nomRep dans baseNode.
	 * Recherche sur 3 niveaux de profondeurs dans l'arbre des noeuds.
	 *  
	 * @param baseNode noeud de depart
	 * @param nomRep nom du noeud.
	 * @return
	 */
	public NodeRef findDirectory(NodeRef baseNode, QName nomRep);
	public NodeRef findDirectory(NodeRef baseNode, String nomRep);
	
	/**
	 * Cherche le repertoire nomRep e partir du root du store parametre.
	 * 
	 * @param nomRep
	 * @return null si non trouve
	 */
	public NodeRef findDirectory(QName nomRep);
	public NodeRef findDirectory(String nomRep);

	

}
