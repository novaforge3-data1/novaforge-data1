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

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation standard de l'interface ValidationResultat.
 * @author Rouviere_x
 *
 */
public class SimpleValidationResultat implements ValidationResultat
{
	/**
	 * Liste des erreurs
	 */
	private final List<Throwable> erreurs= new ArrayList<Throwable>();
	/**
	 * Liste des alertes
	 */
	private final List<Throwable> alertes= new ArrayList<Throwable>();
	
	@Override
	public Throwable getErreur()
	{
		if (hasErreur())
			return erreurs.get(0);
		return null;
	}

	@Override
	public boolean hasErreur()
	{
		return erreurs.size() >0;
	}

	public void addErreur(Throwable erreur)
	{
		if (erreur != null)
			erreurs.add(erreur);
	}

	@Override
	public void addAlerte(Throwable erreur)
	{
		alertes.add(erreur);
		
	}

	@Override
	public Throwable getAlerte()
	{
		if (hasAlerte())
			return alertes.get(0);
		return null;
	}

	@Override
	public List<Throwable> getAlertes()
	{
		return alertes;
	}

	@Override
	public List<Throwable> getErreurs()
	{
		return erreurs;
	}

	@Override
	public boolean hasAlerte()
	{
		return alertes.size()>0	;
	}

	@Override
	public void add(ValidationResultat verifRes)
	{
		if (verifRes != null)
		{
			if (verifRes.hasAlerte())
				alertes.addAll(verifRes.getAlertes());
			if (verifRes.hasErreur())
				erreurs.addAll(verifRes.getErreurs());
		}
	}

}
