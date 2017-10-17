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
package net.bull.alfresco.framework.commun.entite.type;

import java.util.Date;

import net.bull.alfresco.framework.commun.util.FormatTools;


/**
 * Description des formats de donnees.
 * @author Rouviere_x
 *
 */
public enum FORMAT 
{
	DATE
	{
		public String convert(Object o)
		{
			if (o instanceof Date)
				return FormatTools.toString((Date)o);
			return null;
		}		
	},
	ENTIER 
	{
		@Override
		public String convert(Object o)
		{
			if (o instanceof Number)
			{
				long val = ((Number)o).longValue();
				return FormatTools.entierFormat.format(val);
			}
			return null;
		}
	},
	MONAITAIRE 
	{
		@Override
		protected String convert(Object o)
		{
			if (o instanceof Number)
			{
				Double val = ((Number)o).doubleValue();
				return FormatTools.decimalFormat.format(val);
			}
			return null;
		}
	},
	CHAR {
		@Override
		protected String convert(Object o)
		{
			if (o instanceof String)
			{
				return (String)o;				
			}
			if (o instanceof StringBuffer)
			{
				return o.toString();
			}
			return null;
		}
	},
	DOUBLE
	{
		@Override
		protected String convert(Object o)
		{
			if (o instanceof Number)
			{
				Double val = ((Number)o).doubleValue();
				return FormatTools.decimalFormat.format(val);
			}
			return null;
		}
	}, 
	INCONNU 
	{
		@Override
		protected String convert(Object o)
		{
			// TODO Auto-generated method stub
			return null;
		}
	}, 
	DATE_HEURE 
	{
		@Override
		protected String convert(Object o)
		{
			if (o instanceof Date)
			{
				Date d = (Date)o;
				return FormatTools.toString(d) +" "+FormatTools.toTime(d);
			}
			return null;
		}
	}, 
	HEURE 
	{
		@Override
		protected String convert(Object o)
		{
			if (o instanceof Date)
			{
				Date d = (Date)o;
				return FormatTools.toTime(d);
			}
			return null;
		}
	},
	HTML
	{
		/**
		 * 
		 */
		public String convert(Object o)
		{
			String val = o.toString();
			val = val.replace("\n","<br/>");
			return val;
		}		
	};
	
	/**
	 * To String par defaut
	 * @param o
	 * @return
	 */
	public String toString(Object o)
	{
		if (o == null)
			return null;
		String defaut = convert(o);
		if (defaut == null)
			return new String(o.toString());
		return new String(defaut);
	}
	
	/**
	 * Conversion par defaut
	 * @param o
	 * @return
	 */
	protected abstract String convert(Object o);
}
