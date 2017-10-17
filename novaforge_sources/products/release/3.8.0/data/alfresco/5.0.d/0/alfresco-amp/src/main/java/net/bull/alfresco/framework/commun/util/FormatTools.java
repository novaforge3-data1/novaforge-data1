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


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Classe gerant le formatage des donnees.
 * format d'affichage :
 * <ul>
 * <li> des dates {@value #dateFormat} et {@link #toString(Date)}</li>
 * <li> des entiers {@value #entierFormat}</li>
 * <li> des decimaux {@value #decimalFormat}</li>
 * </ul>
 * 
 * Gere la conversion :
 * <ul>
 * <li>String -> Boolean {@link #parseBoolean(String)} </li>
 * <li>String -> Date {@link #parseDate(String)}</li>
 * </ul>
 * 
 * 
 */
public abstract class FormatTools
{
	   /** 
	    * Format d'affichage d'une date. 
	    * 01/12/2005 par exemple 
	    */
	   public static SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

	   public static SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm:ss" );
	   
	   /** Format d'affichage d'un float. */
	   public static DecimalFormat decimalFormat = new DecimalFormat( "#,##0.00" );

	   /** Format d'affichage d'un nombre entier. */
	   public static DecimalFormat entierFormat = new DecimalFormat( "###0" );

	   /** Tableau contenant les differents formats de date et heure acceptes dans l'application. */
	   public static String[] allFormatDate =
	       {
	       // mixtes
	       "d/M/yy H:m",
	       "d/M/yy H'H'm",
	       "d/M/yy H'h'm",
		   "d.M.yy H:m",
	       "d.M.yy H'H'm",
	       "d.M.yy H'h'm",
	       "d,M,yy H:m",
	       "d,M,yy H'H'm",
	       "d,M,yy H'h'm",
	       "d M yy H:m",
	       "d M yy H'H'm",
	       "d M yy H'h'm",
	       "d-M-yy H:m",
	       "d-M-yy H'H'm",
	       "d-M-yy H'h'm",
	       // Dates uniquement
	       "d.M.yy",
	       "d,M,yy",
	       "d/M/yy",
	       "d M yy",
	       "d-M-yy",
	       // heures uniquement
	       "H:m",
	       "H'H'm",
	       "H'h'm"
	   };
	/**
	 * Parse la chaine representant un boolean et retourne un objet de type Boolean. Les valeurs
	 * acceptees sont les suivantes : oui/O/true/1
	 * 
	 * @param value
	 *            chaine e transformer en Boolean.
	 * @return Boolean associe e la chaine passee en parametre.
	 * @exception ParseException
	 *                est levee si le parse est impossible.
	 */
	public static Boolean parseBoolean(String value) throws ParseException
	{
		Boolean bValue = Boolean.FALSE;
		if (value != null && value.length() > 0)
		{
			if (value.equalsIgnoreCase("O") 
				|| value.equalsIgnoreCase("true") 
				|| value.equalsIgnoreCase("1")
				|| value.equalsIgnoreCase("oui") )
			{
				bValue = Boolean.TRUE;
			}
		}
		return bValue;
	}
	
	/**
	 * Parse la chaine representant un integer et retourne un objet de type Boolean. Si le nombre
	 * est invalide, retourne null
	 * 
	 * @param value chaine e transformer en Integer.
	 * @return Integer associe e la chaine passee en parametre.
	 * @exception ParseException
	 *                est levee si le parse est impossible.
	 */
	public static Integer parseInteger(String value) throws ParseException
	{
		try
		{
			return new Integer(value);
		}
		catch(NumberFormatException e)
		{
			return null;
		}
		
	}
	
	
	
	/**
	 * Retourne une date e partir de la valeur passee en parametre.
	 * @param value != null
	 * @return une date null si le parametre est null
	 * @throws ParseException Si la chaine est mal formate
	 */
	public static Date parseDate(String value) throws ParseException
	{

	      Date d = null;
	      if ( value != null && !value.equals( "" ) )
	      {
	         SimpleDateFormat sdf = null;
	         int i = 0;
	         boolean bDone = false;
	         while ( !bDone && i < allFormatDate.length )
	         {
	            sdf = new SimpleDateFormat( allFormatDate[i] );
	            sdf.setLenient( false );
	            try
	            {
	               d = sdf.parse( value );
	               bDone = true;
	            }
	            catch ( ParseException e )
	            {}
	            i++;
	         }
	         if ( i == allFormatDate.length && !bDone )
	         {//tous le tableau est parcourus et la chaine n'est pas parsee
	            throw new ParseException( "Impossible de transformer la date " + value, i );
	         }
	      }
	      return d;
	}
	
	/**
	 * Convertie une date suivant le format general {@link #dateFormat}
	 * renvoie null si le parametre est null
	 * @param d
	 * @return
	 */
	public static String toString(Date d)
	{
		if (d != null )
			return dateFormat.format(d);
		return null;
	}
	
	/**
	 * Donne l'heure de la date
	 * @param d
	 * @return
	 */
	public static String toTime(Date d)
	{
		if (d != null )
			return timeFormat.format(d);
		return null;
	}
	
	/**
	 * Transforme un objet en String.
	 * 
	 * @param o
	 * @return une representation String de l'objet
	 */
	public static String parse(Object o)
	{
		// en attendant d'avoir mieux
		if (o!= null)
			return o.toString();
		else
			return null;
	}
}
