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

import net.bull.alfresco.framework.commun.entite.type.Versionnable;
import net.bull.alfresco.framework.commun.util.Tools;


/**
 * Gestion de base d'un numero de version.
 * @author Rouviere_x
 *
 */
public class DefautVersion implements Versionnable
{

	private String numero;
	private int revision;
	private String patch;
	private int mineur;
	private int majeur;
	
	/**
	 * Constructeur par defaut de la version 0.0.0
	 */
	public DefautVersion()
	{
		this(null);
	}
	
	/**
	 * Initialise le numero de version.
	 * @param numero
	 */
	public DefautVersion(String numero)
	{
		this.numero = numero;
	}

	
	@Override
	public String getNumero()
	{
		if (numero == null)
		{
			numero = majeur+Versionnable.SEP+mineur+Versionnable.SEP+revision;
			if (Tools.isSet(patch))
			{
				numero += Versionnable.SEP+patch;
			}
		}
		return numero;
	}

	public String toString()
	{
		return getNumero();
	}
	
	@Override
	public void setMajeur(int majeur)
	{
		this.majeur = majeur;
	}

	@Override
	public void setMineur(int mineur)
	{
		this.mineur = mineur;		
	}

	@Override
	public void setPatch(String patch)
	{
		this.patch = patch;
		
	}

	@Override
	public void setRevision(int revision)
	{
		this.revision = revision;
		
	}

}
