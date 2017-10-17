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

import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;


/**
 * Classe d'aiguillage pour le chargement des messages
 * 
 * @author Rouviere_x
 *
 */
public class MessageLoader extends ClassLoader
{

	Logger log = Logger.getLogger(MessageLoader.class);
	
	
	/**
	 * Surcharge du get ressources pour chercher les messages dans :
	 * <ul>
	 *  <li> A la racine du classpath</li>
	 *  <li>dans classpath:messages/nom.court</li>
	 *  <li> dans classpath:path/de/la/classe/</li>
	 *  </ul>
	 *  
	 * @param name nom complet de la classe
	 * @return
	 */
	@Override
	public URL getResource(String name)
	{
		URL res = super.getResource(name);
		String shortName = name; // par defaut le nom court c'est le nom
		String path = "messages"; // par defaut le path c'est messages
		if (res == null)
		{
			shortName = getShortName(name);
			try
			{
				String cp = "classpath:"+path+"/"+shortName;
				res = ResourceUtils.getURL(cp);
				log.debug(cp);
			}
			catch (FileNotFoundException e)
			{
			}			
		}
		if (res == null)
		{
			String cp = "classpath:"+name.replace(".", "/");
			try
			{
				res = ResourceUtils.getURL(cp);
				log.debug(cp);
			}
			catch (FileNotFoundException e)
			{
			}			
		}			
		if (res == null)
		{
			try
			{
				res = ResourceUtils.getURL("classpath:"+name);
				log.debug("classpath:"+name);
			}
			catch (FileNotFoundException e)
			{
			}			
		}		
		
		return res;
	}


	/**
	 * Recuperation du nom court
	 * @param name
	 * @return
	 */
	private String getShortName(String name)
	{
		int li=0;
		if ( (li = name.lastIndexOf("/")) != -1 )
		{// cas test/pack/nom.ext
			if (li+1 < name.length())
				return name.substring(li+1);			
		}
		if ((li = name.lastIndexOf(".")+1) <name.length())
		{// cas test.pack.Nom
			return name.substring(li);
		}
		return "defaut";
	}
}
