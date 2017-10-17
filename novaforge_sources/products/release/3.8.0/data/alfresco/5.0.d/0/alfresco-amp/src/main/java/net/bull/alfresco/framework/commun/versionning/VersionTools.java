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
package net.bull.alfresco.framework.commun.versionning;

import java.util.Arrays;

import net.bull.alfresco.framework.commun.entite.annotation.DematVersion;
import net.bull.alfresco.framework.commun.entite.type.Versionnable;
import net.bull.alfresco.framework.commun.util.Tools;

/**
 * Pour faire des numeros de version facile.
 * 
 * @author Rouviere_x
 *
 */
public abstract class VersionTools
{

	/**
	 * Generation d'un objet de versionnable.
	 * @param numero
	 * @return
	 */
	public static Versionnable getVersion(String numero)
	{
		if (Tools.isSet(numero))
		{
			return new DefautVersion(numero);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Donne un Versionnable e partir d'un tableau de numero
	 * @param val
	 * @return
	 */
	public static Versionnable getVersion(int[] val)
	{
		Versionnable res= new DefautVersion();
		if (val != null)
		{
			int cpt =0;
			if (val.length >cpt)
				res.setMajeur(val[cpt++]);
			if (val.length >cpt)
				res.setMineur(val[cpt++]);
			if (val.length >cpt)
				res.setRevision(val[cpt++]);
			if (val.length >cpt)
				res.setPatch(Tools.concatArrays(Arrays.copyOfRange(val, cpt, val.length)));			
		}
		return res;
	}
	/**
	 * Renvoie un objet versionnable e partir d'un objet annote.
	 * 
	 * @param obj
	 * @return
	 */
	public static Versionnable getVersion(Object obj)
	{
		Versionnable res = null;
		if (obj != null)
		{
			Class<?> c = obj.getClass();
			
			DematVersion ver = c.getAnnotation(DematVersion.class);
			if (ver != null)
			{
				res = new DefautVersion();
				res.setMajeur(ver.majeur());
				res.setMineur(ver.mineur());
				res.setRevision(ver.revision());
				res.setPatch(ver.patch());				
			}
			else
			{
				res= new DefautVersion(obj.getClass().getPackage().getImplementationVersion());
			}
		}		
		return res;
	}
}
