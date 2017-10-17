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

import java.text.ParseException;
import java.util.List;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.CronScheduledQueryBasedTemplateActionDefinition;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * Classe mere pour les traitements asynchrones.
 * 
 * Pour idee : {@link CronScheduledQueryBasedTemplateActionDefinition}
 * @author Rouviere_x
 *
 */
public abstract class AbstractJob extends AbstractScheduledAction
{

	protected NodeService nodeService;
	/**
	 * Un logger pour la route.
	 */
	protected Logger log = Logger.getLogger(getClass());
	

	private String triggerName;

	private String triggerGroup;

	private String jobName;

	private String jobGroup;

	
    /*
     * The cron expression
     */
    private String cronExpression;
    
    private String queryTemplate;
    

    /*
     * The scheduler
     */
    private Scheduler scheduler;
	private TemplateService templateService;
	private SearchService searchService;
    
	
	/**
	 * Le constructeur de la classe mere est important
	 */
	public AbstractJob()
	{
		super();
	}

	@Override
	public Action getAction(NodeRef nodeRef)
	{// voir pour simplifier
		return getTemplateActionDefinition().getAction(nodeRef);
	}

	@Override
	public abstract List<NodeRef> getNodes();

	@Override
    public Trigger getTrigger()
    {
		log.debug("creation du trigger");
        try
        {
            return new CronTrigger(getTriggerName(), getTriggerGroup(), getCronExpression());
        }
        catch (final ParseException e)
        {
            throw new InvalidCronExpression("Invalid chron expression: n" + getCronExpression());
        }
    }


	@Override
	public String getJobGroup()
	{
		return jobGroup;
	}

	@Override
	public String getJobName()
	{
		return jobName;
	}

	@Override
	public String getTriggerGroup()
	{
		return triggerGroup;
	}

	@Override
	public String getTriggerName()
	{
		return triggerName;
	}

	@Override
	public void setJobGroup(String jobGroup)
	{
		this.jobGroup = jobGroup;
	}

	@Override
	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	@Override
	public void setTriggerGroup(String triggerGroup)
	{
		this.triggerGroup = triggerGroup;
	}

	@Override
	public void setTriggerName(String triggerName)
	{
		this.triggerName =triggerName;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
			// initialisation par defaut ici
		
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public NodeService getNodeService()
	{
		return nodeService;
	}

	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}

	public String getQueryTemplate()
	{
		return queryTemplate;
	}

	public void setQueryTemplate(String queryTemplate)
	{
		this.queryTemplate = queryTemplate;
	}
	
    /**
     * Get the scheduler.
     * @return - the scheduler.
     */
    public Scheduler getScheduler()
    {
        return scheduler;
    }

    /**
     * Set the scheduler.
     * @param scheduler
     */
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    /**
     * Get the template service.
     * 
     * @return - the template service.
     */
    public TemplateService getTemplateService()
    {
        return templateService;
    }

    /**
     * Set the template service.
     * 
     * @param templateService
     */
    public void setTemplateService(TemplateService templateService)
    {
        this.templateService = templateService;
    }
    
    /**
     * Get the search service.
     * 
     * @return - the serach service.
     */
    public SearchService getSearchService()
    {
        return searchService;
    }

    /**
     * Set the search service.
     * 
     * @param searchService
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
}
