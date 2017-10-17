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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Methode pour faire des copies.
 *  
 * @author Rouviere_x
 *
 */
public abstract class CopyTools
{
	/**
	 * Taille de base
	 */
	public static final int BUFFER_SIZE = 4096;
	
	/**
	 * Lecture du fichier name
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public static byte[] readFile(String name) throws IOException
	{
		File f = new File(name);
		if (f.exists())
			return readFile(f);
		else
			return null;
	}
	/**
	 * Renvoie le contenu du fichier sous la forme d'un tableua de byte 
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	public static byte[] readFile(File file) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		copy(fis,baos);
		return baos.toByteArray();
	}
	
	/**
	 * Copy dans l'os e moelle...
	 * 
	 * @param tab
	 * @param fos
	 * @throws IOException 
	 */
	public static void copy(byte[] tab, OutputStream out) throws IOException
	{
		out.write(tab);
	}

	/**
	 * Copy de stream e stream.
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) 
		{
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		
	}
	/**
	 * Methode pour copier un fichier dans un repertoire
	 * Le repertoire doit existe. 
	 * @param src
	 * @param dest
	 */
	public static void copy(File src, File dirDest) throws IOException
	{
		FileInputStream fis = new FileInputStream(src);
		File dest = new File(dirDest, src.getName());
		dest.createNewFile();
		FileOutputStream fos = new FileOutputStream(dest);
		copy(fis,fos);		
	}	
}
