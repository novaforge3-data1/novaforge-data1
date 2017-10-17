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

import java.util.Comparator;
import java.util.StringTokenizer;

import net.bull.alfresco.framework.commun.entite.type.Versionnable;

/**
 * Classe permettant d'avoir un comparateur de numero de Version.
 * 2.10.0 et un numero de version superieur a 2.2.0.
 * Or le comparateur par defaut du TreeSet prend l'ordre alphabetique et on a
 * 2.2.0 > 2.10.0
 * La comparaison ne se fera que sur des objets de type String
 * Le format des chaines sera de type [0..9][.-_ ]*
 * Exemple 2.10.0, 2.2.1, 2_10_10 2-45-789-5
 */
public class CompareVersion implements Comparator<Versionnable>
{
	public static int ASC  = 1;
	public static int DESC = -1;

	// sens de la comparaison ( descendant ou montant)
	private final int sens;

	/**
	 * Constructeur par defaut
	 */
	public CompareVersion()
	{
		// par defaut fait une comparaison croissante...
		this(ASC);
	}

	/**
	 * Comparateur avec sens
	 * 
	 * @param sens
	 */
	public CompareVersion(final int sens)
	{
		this.sens = sens;
	}

	/**
	 * Comparaison decroissante ou croissante
	 * 
	 * @param flag
	 *          boolean : true : comparaison decroissante, false sinon
	 */
	public CompareVersion(final boolean flag)
	{
		this(flag ? DESC : ASC);
	}

	/**
	 * Compare A(src) et B(targ) A et B sont != null A et B sont decoupees en
	 * token. Les tokens sont transformes en int et on compare les deux valeurs si
	 * erreur (NumberFormatException) alors on compare les chaines avec compareTo
	 * 
	 * @return -1 si A < B; 0 si A=B; 1 si A>B
	 * @param src
	 *          String
	 * @param targ
	 *          String
	 */
	private int compareTo(final String src, final String targ)
	{
		final String A = src;
		final String B = targ;

		// test egalite
		if (A.equals(B))
		{
			return 0;
		}
		// test A null et B non null => A < B
		if (A.equals(""))
		{
			return -1;
		}
		// test B null
		if (B.equals(""))
		{
			return 1;
		}

		// A != B
		final StringTokenizer tokA = new StringTokenizer(A, "._- ", false);
		final StringTokenizer tokB = new StringTokenizer(B, "._- ", false);

		final int nbTokA = tokA.countTokens();
		final int nbTokB = tokB.countTokens();

		int borneFin; // borne de fin
		int nbTokDif; // nb tok different + si A en a plus - sinon
		if (nbTokA == nbTokB)
		{
			// On est dans le cas standard X1.Y1.Z1 a compare avec X2.Y2.Z2
			borneFin = nbTokA;
			nbTokDif = 0;
		}
		else
		{
			if (nbTokA > nbTokB)
			{
				borneFin = nbTokB;
				nbTokDif = 1;
			}
			else
			{
				borneFin = nbTokA;
				nbTokDif = -1;
			}
		}
		for (int i = 0; i < borneFin; i++)
		{
			int res = 0;
			final String a = tokA.nextToken();
			final String b = tokB.nextToken();
			try
			{
				final int vala = Integer.parseInt(a);
				final int valb = Integer.parseInt(b);
				res = vala - valb;
				if (res == 0)
				{
					res = a.length() - b.length();
				}
			}
			catch (final java.lang.NumberFormatException e)
			{
				res = a.compareTo(b);
			}

			if (res != 0)
			{
				return res;
			}
		}
		return nbTokDif;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + sens;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CompareVersion other = (CompareVersion) obj;
		if (sens != other.sens)
		{
			return false;
		}
		return true;
	}

	/**
	 * Compare deux objets Appel de compareDef(String, String) * sens
	 * sens = 1 si mode croissant
	 * sens =-1 si mode decroissant
	 * 
	 * @param obj1
	 *          Versionnable
	 * @param obj2
	 *          Versionnable
	 * @return int 0 si egaux 1 si obj1 > obj2 -1 si obj1< obj2 cas ASC
	 */
	@Override
	public int compare(final Versionnable obj1, final Versionnable obj2)
	{
		if ((obj1 != null) && (obj2 != null))
		{
			return compareTo(obj1.getNumero(), obj2.getNumero()) * sens;
		}
		else
		{
			if ((obj1 == null) && (obj2 == null))
			{
				return 0;
			}
			if (obj1 == null)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
	}
}
