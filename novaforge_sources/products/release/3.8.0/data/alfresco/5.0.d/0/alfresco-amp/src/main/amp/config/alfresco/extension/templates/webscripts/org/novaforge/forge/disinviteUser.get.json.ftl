<#if !error??>
{
   "project":  
   {
      "userName" : "${userName}",
      "projectName" : "${projectName}",
      "role" : "${role}",
      "message" : "${message}"
   }
}
<#else>
<#include "/org/novaforge/forge/error/error.get.json.ftl" >
</#if>