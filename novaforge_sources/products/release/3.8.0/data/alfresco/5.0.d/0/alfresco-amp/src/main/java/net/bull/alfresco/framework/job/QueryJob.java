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
package net.bull.alfresco.framework.job;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.repo.action.scheduled.CronScheduledQueryBasedTemplateActionDefinition;
import org.alfresco.repo.action.scheduled.TemplateActionDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.log4j.Logger;

/**
 * 
 * @author Rouviere_x
 * 
 */
public class QueryJob extends CronScheduledQueryBasedTemplateActionDefinition
{
	/**
	 * Un logger pour la route.
	 */
	protected Logger log = Logger.getLogger(getClass());

	private String actionName;
	
	/**
	 * Constructeur de la classe.
	 */
	public QueryJob()
	{
		super();
		initDefault();
	}

	/**
	 * Initialisation par defaut.
	 * 
	 */
	private void initDefault()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public List<NodeRef> getNodes()
	{
		LinkedList<NodeRef> nodeRefs = new LinkedList<NodeRef>();

		// Build the actual query string
		String queryTemplate = getQueryTemplate();
		// Execute the query
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(getQueryLanguage());
		sp.setQuery(queryTemplate);
		for (String storeRef : getStores())
		{
			sp.addStore(new StoreRef(storeRef));
		}

		ResultSet results = null;
		// Transform the reults into a node list
		// ResultSet results = null;
		try
		{
			results = getSearchService().query(sp);
			for (ResultSetRow row : results)
			{
				nodeRefs.add(row.getNodeRef());
			}
		}
		finally
		{
			if (results != null)
			{
				results.close();
			}
		}
		log.info("getNodes()-> liste de "+nodeRefs.size());
		return nodeRefs;
	}

	/**
	 * Construit un TemplateActionDefinition par defaut si il n'est pas renseigne.
	 *  
	 */
	@Override
    public TemplateActionDefinition getTemplateActionDefinition()
    {
		TemplateActionDefinition superTemplate = super.getTemplateActionDefinition();
		if (superTemplate == null)
		{
			SimpleActionDefinition templateActionDefinition = new SimpleActionDefinition();
			templateActionDefinition.setActionService(getActionService());
			templateActionDefinition.setActionName(actionName);
			setTemplateActionDefinition(templateActionDefinition);
			superTemplate = templateActionDefinition;
		}
		return superTemplate;
    }
	
	/**
	 * Positionnement de l'action Name.
	 * @param actionName
	 */
	public void setActionName(String actionName)
	{
		this.actionName = actionName;
	}
	
	public String getActionName()
	{
		return actionName;
	}
}
