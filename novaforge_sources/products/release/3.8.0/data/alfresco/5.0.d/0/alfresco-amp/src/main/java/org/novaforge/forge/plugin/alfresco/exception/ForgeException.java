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
package org.novaforge.forge.plugin.alfresco.exception;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Message error for the exceptions
 * 
 * @author Cadet_r
 */
@SuppressWarnings("serial")
public class ForgeException extends Exception
{

   public static int ERR_FORGE            = 10;
   public static int ERR_PARAM_FORGE      = 1;
   public static int ERR_USER_EXIST       = 2;
   public static int ERR_PROJET_EXIST     = 3;
   public static int ERR_PROJET_NOT_EXIST = 4;
   public static int ERR_USER_NOT_EXIST   = 5;

   /**
    * Constructor
    * 
    * @param code
    */
   public ForgeException(int code)
   {
      super(I18NUtil.getMessage(getKey(code)));
   }

   /**
    * Constructor
    * 
    * @param code
    */
   public ForgeException()
   {
      super(I18NUtil.getMessage(getKey(ERR_FORGE)));
   }

   private static String getKey(int code)
   {
      return "message.creation.m" + code;
   }
}
