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
package net.bull.alfresco.framework.action;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;

/**
 * Execution d'une action par le user system.
 * il suffit d'implementer l'action.
 * 
 * @author Rouviere_x
 *
 */
public abstract class BaseActionSystem extends BaseAction implements RunAsWork<String>
{

	@Override
	protected void internalTraitement()
	{
		AuthenticationUtil.runAs(this, AuthenticationUtil.SYSTEM_USER_NAME);		
	}

	@Override
	public String doWork() throws Exception
	{
		try
		{
			log.debug("doWork()-> Debut");
			String usr = AuthenticationUtil.getFullyAuthenticatedUser();
			AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.SYSTEM_USER_NAME);
			//UserTransaction usrTx = transactionService.getUserTransaction();	
			//usrTx.begin();			
			internalWork();
			//usrTx.commit();
			//pour avoir remettre le user initial
			AuthenticationUtil.setFullyAuthenticatedUser(usr);			
			log.debug("doWork()-> Fin");
		}
		finally
		{
				
		}
		return "succes";
		
	}

	/**
	 * C'est elle qui fait le boulot.
	 * @throws Exception
	 */
	protected  abstract void internalWork() throws Exception;
	

}
