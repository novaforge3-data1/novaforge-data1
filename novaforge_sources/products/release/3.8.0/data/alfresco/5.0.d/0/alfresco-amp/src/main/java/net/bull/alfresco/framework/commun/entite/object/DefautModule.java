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


import net.bull.alfresco.framework.commun.entite.type.Module;
import net.bull.alfresco.framework.commun.entite.type.Versionnable;
import net.bull.alfresco.framework.commun.util.Tools;
import net.bull.alfresco.framework.commun.versionning.VersionTools;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Implementation basique d'un module
 * @author Rouviere_x
 *
 */
public class DefautModule implements Module
{

	private String version;
	private String id;
	private String title;
	private String description;

	/**
	 * Constructeur par defaut
	 */
	public DefautModule()
	{
		this("defautModule", "0.0.000");
	}
	
	/**
	 * Initialisation des champs
	 * @param id
	 * @param version
	 */
	public DefautModule(String id, String version)
	{
		this.id = id;
		this.version = version;
		this.title = "Module "+id;
		this.description = title + " version "+version;
	}


	@Override
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	
	@Override
	public Versionnable getVersion()
	{
		return VersionTools.getVersion(version);
	}

	/**
	 * Positionne un numero de version
	 */
	public void setVersion(int[] nums)
	{
		if (nums != null)
		{
			version = Tools.concatArrays(nums);			
		}
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	@Override
	public int compareTo(Module o)
	{
		if (o != null)
			return getId().compareTo(o.getId());
		return 1;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description= description;
	}

	
	@Override
	public String getTitle()
	{
		return title;
	}
	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("description", getDescription());
		jsonObject.put("id", getId());
		jsonObject.put("version", getVersion());
		jsonObject.put("title", getTitle());
		return jsonObject;
	}

}
