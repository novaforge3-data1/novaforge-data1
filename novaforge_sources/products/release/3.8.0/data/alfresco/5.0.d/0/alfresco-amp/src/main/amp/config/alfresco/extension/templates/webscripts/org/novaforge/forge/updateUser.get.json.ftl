<#if !error??>
{
   "user":  
   {
      "userName" : "${userName}",
      "message" : "${message}"
   }
}
<#else>
<#include "/org/novaforge/forge/error/error.get.json.ftl" >
</#if>