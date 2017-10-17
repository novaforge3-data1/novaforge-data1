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
package net.bull.alfresco.framework.commun.exception;

import net.bull.alfresco.framework.commun.util.Message;
import net.bull.alfresco.framework.commun.util.Tools;

import org.apache.log4j.Logger;




/**
 * Classe mere pour les exceptions du framework bull alfresco
 * @author Rouviere_x
 *
 */
public class DematException extends Exception
{

	private static final long serialVersionUID = 9026545703447145195L;
	
	Logger log = Logger.getLogger(DematException.class);
	
	public static final String ERR_DEFAULT = "erreur.code.defaut";
	public static final String ERR_BAD_TYPE = "DematExeption.ERR_BAD_TYPE";

	private Message mess; 
	
	private String code;
	private Object[] param;
	
	/**
	 * Constructeur par defaut.
	 */
	public DematException()
	{
		this(ERR_DEFAULT, null, null);
	}

	/**
	 * Constructeur avec un message.
	 * @param message description de l'erreur.
	 */
	public DematException(String message)
	{
		super(message);
	}

	/**
	 * Constructeur avec une cause.
	 * @param message description de l'erreur.
	 */
	public DematException(Throwable cause)
	{
		super(Tools.getMessage(cause));
	}
	
	/**
	 * Constructeur avec un code et une cause.
	 * @param cause
	 */
	public DematException(String code, Throwable cause)
	{
		this(code, cause, null);
	}

	/**
	 * 
	 * @param code
	 * @param cause
	 * @param param
	 */
	public DematException(String code, Throwable cause, Object[] param)
	{
		super(cause);
		this.code = code;
		this.param = param;
	}

	/**
	 * Construction erreur avec un code message et des parametres
	 * @param code
	 * @param param
	 */
	public DematException(String code, Object[] param)
	{
		this(code, null,param);
	}
	
	@Override
	public String getMessage()
	{
		String mess;
		if (code != null)
		{
			log.debug("Recherche du code :"+code);
			mess = getMessageProp().getMessage(code, param);
			mess = mess.concat(" : Cause (");
			mess = mess.concat(Tools.getMessage(getCause()));
			mess = mess.concat(")");
		}
		else
		{
			mess = super.getMessage();
		}
		return mess;
		
		
	}
	
	
	/**
	 * Renvoie le message bundle 
	 * @return
	 */
	protected  Message getMessageProp()
	{
		if (mess == null)
		{			
			mess = new Message(getClass());
			log.debug("getMessageProp :"+mess);
		}
		return mess;
	}
}
