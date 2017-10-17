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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import net.bull.alfresco.framework.commun.entite.annotation.MetaDataColonne;
import net.bull.alfresco.framework.commun.entite.annotation.MetaDataListe;
import net.bull.alfresco.framework.commun.entite.type.DematItem;
import net.bull.alfresco.framework.commun.entite.type.FORMAT;

import org.apache.log4j.Logger;


/**
 * Implementation par defaut d'une InfoListe.
 * 
 * Construit les info a partir des metaData
 * 
 * @author Rouviere_x
 * @version 1.0
 */
public class DematInfoListe implements InfoListe
{

	Logger log = Logger.getLogger(DematInfoListe.class);
	
	Map<Integer, String> nameCol =new Hashtable<Integer, String>(); 
	Map<Integer,FORMAT> typeCol =new Hashtable<Integer, FORMAT>();
	Map<Integer,String> descCol =new Hashtable<Integer, String>();
	Map<Integer, Method> methodCol = new Hashtable<Integer, Method>();
	
	int size;
	String description;
	
	private Class<? extends DematItem> type;
	
	/**
	 * Constructeur de la classe.
	 * Initialise les infos
	 * @param type
	 */
	public DematInfoListe(Class<? extends DematItem> type)
	{
		this.type = type;
		buildInfo();
	}
	
	@Override
	public FORMAT getFormat(int index)
	{		
		return typeCol.get(index);
	}

	/**
	 * Construit les informations sur E
	 * Si il n'y a pas d'annotations sur E, recherche des annotations sur le type de la classe mere.
	 * @param elem
	 */
	private void buildInfo()
	{
		log.debug("E ="+type);
		try
		{
			MetaDataListe infoListe = type.getAnnotation(MetaDataListe.class);			
			if (infoListe != null)
			{
				extractInfo(infoListe);
			}
			else
			{
				//recherche des infos sur la classe mere
				Class<?>[] typeMere = type.getInterfaces();
				for(Class<?> lt : typeMere)
				{
					MetaDataListe infoListeM = lt.getAnnotation(MetaDataListe.class);
					if (infoListeM != null)
						extractInfo(infoListeM);
				}
			}
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * @param infoListe
	 */
	private void extractInfo(MetaDataListe infoListe)
	{
		description = infoListe.description();
		Method[] allMeth = type.getMethods();
		MetaDataColonne infoCol;
		for(int i=0; i< allMeth.length;i++)
		{ // avantage nous pouvons stocker le nom de la methode pour avoir le champ
			infoCol = allMeth[i].getAnnotation(MetaDataColonne.class);
			if (infoCol != null)
			{
				log.debug("MetaData METH:"+infoCol);
				int pos = infoCol.pos();
				nameCol.put(pos, infoCol.name());
				descCol.put(pos, infoCol.desc());
				typeCol.put(pos, infoCol.format());
				methodCol.put(pos, allMeth[i]);
			}
		}
	}

	@Override
	public int getNbColonne()
	{		
		return nameCol.size();
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public String getDescription(int index)
	{
		return descCol.get(index);
	}

	@Override
	public String getNom(int index)
	{
		return nameCol.get(index);
	}
	
	@Override
	public String getNomMethod(int index)
	{
		return methodCol.get(index).toString();
	}

	@Override
	public Object getObject(DematItem item, int index)
	{		
		Object o = getValeur(item, index);
		return o;
	}

	@Override
	public String getString(DematItem item, int index)
	{
		Object o = getValeur(item, index);
		FORMAT f = getFormat(index);
		return f.toString(o);
	}

	/**
	 * Verification du type d'item
	 * Recuperation de la methode
	 * Recuperation de la valeur
	 * 
	 * @param item
	 * @param index
	 * @return Object
	 */
	private Object getValeur(DematItem item, int index)
	{
		Object val=null;
		//Verification du type d'item
		this.verifType(item);		
		//Recuperation de la methode correspondant e la colonne
		Method m = null;
		m = methodCol.get(index);

		try
		{
			val = m.invoke(item, new Object[0]);
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Erreur d'argument", e);
		}
		catch (IllegalAccessException e)
		{
			log.warn("La methode (ou l'objet) annotee n'est pas accessible ("+item+"-"+m.getName()+")", e);
		}
		catch (InvocationTargetException e)
		{
			log.warn("Erreur sur l'objet "+item, e);
		}

		return val;
	}
	
	/**
	 * Verifie que le type de l'astreInfo correspond bien avec l'item passe en parametre.
	 * @param item
	 * @throws InvalidItemException
	 * @throws ClassNotFoundException 
	 */
	private void verifType(DematItem item)
	{
			try
			{
				if(!(Class.forName(type.getName()).isInstance(item)))
				{
					log.error("Erreur de type "+type.getName()+" <-> "+item );
				}
			}
			catch (ClassNotFoundException e)
			{
				log.error("Type "+type.getName()+" not found !!!");
			}
	}
}
