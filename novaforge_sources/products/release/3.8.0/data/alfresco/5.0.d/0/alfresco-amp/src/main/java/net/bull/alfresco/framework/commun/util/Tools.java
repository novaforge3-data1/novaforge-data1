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

import static java.lang.Math.abs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import org.apache.log4j.Logger;


/**
 * Classe pour faciliter certaines operations sur les objets String.
 * 
 * <ul>
 * 	<li>Generation d'un message d'erreur simplifie {@link #getMessage(Throwable)}</li>
 * 	<li> Test pour savoir si une variables String est initialise {@link #isSet(String)}</li>
 *  <li> Generation d'un nombre aleatoire {@link #randomInt(int)}</li> 
 * </ul>
 * 
 * 
 * @author Rouviere_x
 * @version 1.0 
 */
public abstract class Tools
{
	static Logger log = Logger.getLogger(Tools.class);

	
	/**
	 * Constante pour definir le signe des nombres alleatoire
	 *
	 */
	public static enum Signe {POSITIF,NEGATIF,ALL};
	private static Random rnd = new Random(19780817);
	private static enum alpha {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z};
	private static int sizeAlpha = alpha.values().length;
	
	/**
	 * Renvoie le message d'erreur de la classe.
	 * Le message est de la forme suivante :
	 * [e.ClassName] e.Message 
	 * Si l'erreur contient une cause le message suivant est ajoute :
	 * --cause([e.cause.ClassName] e.cause.Message)
	 * 
	 * @param e une erreur
	 * @return message d'erreur formate.
	 */
	public static String getMessage(Throwable e)
	{
		String res;
		if (e != null)
		{
			res = "[" + e.getClass() + "] " + e.getMessage();
			Throwable cause = null;
			if ((cause = e.getCause()) != null)
			{
				res += "--cause(" + "[" + cause.getClass() + "] " + cause.getMessage() + ")";
			}
		}
		else
		{
			res = "Aucun message";
		}
		return res;
	}

	/**
	 * Teste si la valeur est positionne.
	 * 
	 * @param value valeur e tester
	 * @return vrai si la valeur est differente de nulle et de ""
	 */
	public static boolean isSet(String value)
	{		
		return value != null && value.length()>0;
	}

	

	
	/**
	 * Renvoie un entier "aleatoire" du signe demande.
	 * 
	 * @param positif
	 * @return
	 */
	public static int randomInt(Signe s)
	{
		int res = rnd.nextInt();
		if (s == Signe.POSITIF)
			return abs(res);
		else if (s== Signe.NEGATIF)			
			return 0-abs(res);
		else
		return res;
	}

	/**
	 * Renvoie un tableau de lettre de la taille.
	 * Les lettres sont generes de maniere aleatoire
	 * @param i
	 * @return
	 */
	public static char[] randomAlpha(int size)
	{
		char[] res = new char[size];
		for(int i=0; i<size; i++)
		{
			res[i] = alpha.values()[rnd.nextInt(sizeAlpha)].toString().charAt(0);
		}
		return res;
	}


	/**
	 * Renvoie la date du jour dans une suite de nombre.
	 * @return
	 */
	public static String getLongDate()
	{
		Calendar time = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		sb.append(time.get(Calendar.YEAR));
		sb.append(time.get(Calendar.MONTH));
		sb.append(time.get(Calendar.DAY_OF_YEAR));
		sb.append(time.get(Calendar.HOUR_OF_DAY));
		sb.append(time.get(Calendar.MINUTE));
		sb.append(time.get(Calendar.SECOND));
		sb.append(time.get(Calendar.MILLISECOND));
		return sb.toString();
	}

	/**
	 * Renvoie un entier au hazard (waaazaaaard) entre 0 et max  
	 * @param i
	 * @return
	 */
	public static int randomInt(int max)
	{
		rnd.nextInt(max);
		return rnd.nextInt(max);
	}
	
	/**
	 * Cette methode permet de tester que l'URL du WSDL
	 *  passer dans le fichier de configuration est valide
	 * @param url
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ParapheurException 
	 */
	public static void testUrl(String url) throws MalformedURLException, IOException 
	{		
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.connect();
		conn.disconnect();
	}

	/**
	 * Renvoie la valeur si elle n'est pas nulle renvoie valDef sinon
	 * 
	 * @param valeur
	 * @param valDef
	 * @return
	 */
	public static String valDefIfNull(String valeur, String valDef)
	{
		return valeur == null ? valDef : valeur;
	}
	
	/**
	 * Concatene un tableeau dans une chaine de carctere avec le . comme separateur
	 * @param tab
	 * @return
	 */
	public static String concatArrays(int[] tab, String sep)
	{
		String res = new String();
		if (tab != null)
		{
			for(Object o: tab)
			{
				res += o +sep;
			}
			if (res.length() > sep.length() && res.indexOf(sep)>0)
				res = res.substring(0, res.length() - sep.length());
		}
		return res;
	}
	public static String concatArrays(int[] tab)
	{
		return concatArrays(tab, ".");
	}
}
