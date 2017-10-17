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
package net.bull.alfresco.framework.commun.util;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Boite e outils pour faire du JSon.
 * 
 * @author Rouviere_x
 *
 */
public class JSONTools
{

	/**
	 * Pour pas avoir d'erreur de lecture.
	 * @param key
	 * @param jsObj
	 * @return
	 */
	public static Serializable readString(String key, JSONObject jsObj)
	{
		if (jsObj != null && key != null)
		{
			try
			{
				return jsObj.getString(key);
			}
			catch(JSONException e)
			{ // le JSOn object ne contient pas la cle key!!!
				return null; // ou "" a voir.
			}
		}
		return null;
	}

}
