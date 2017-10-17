<%--
  - Copyright (c) 2004, 2005 Polarion Software, All rights reserved.
  - Copyright (C) 2011-2014, BULL SAS, NovaForge Version 3 and above.
  - Email: community@polarion.org
  -
  - This program and the accompanying materials are made available under the
  - terms of the Apache License, Version 2.0 (the "License"). You may not use
  - this file except in compliance with the License. Copy of the License is
  - located in the file LICENSE.txt in the project distribution. You may also
  - obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  -
  -
  - POLARION SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES
  - ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED,
  - INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  - FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. POLARION SOFTWARE
  - SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
  - OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
  --%>
<%@ page import="org.polarion.svnwebclient.configuration.ConfigurationProvider"%>  
<jsp:useBean id="bean" scope="request" type="org.polarion.svnwebclient.web.controller.RestrictedAccessBean"/>

<table cellspacing="0" cellpadding="0" width="100%">
    <tr>
<%
	if (!ConfigurationProvider.getInstance().isEmbedded() && !ConfigurationProvider.getInstance().isEmbeddedNovaforge()) {
%>	                
        <td align="left">
        	<input type="button" class="button" value="Ok" onclick="javascript:window.location='<%=bean.getRestrictLoginUrl()%>'"/>                     	
        </td>
<%
	}
%>                    
        <td align="left" style="padding-left:10px;">
       		<input type="button" class="button" value="Cancel" onclick="javascript:window.location='<%=bean.getFullRootPageUrl()%>'"/>                     	
        </td>
        <td width="100%"/>
    </tr>
</table>
