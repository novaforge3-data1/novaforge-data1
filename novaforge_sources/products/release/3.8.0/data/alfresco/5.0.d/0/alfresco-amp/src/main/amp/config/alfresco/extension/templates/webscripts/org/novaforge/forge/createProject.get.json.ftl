<#if !error??>
{
	"project":  
	{
		"projectName" : "${projectName}",
		"message" : "${message}"
	}
}
<#else>
<#include "/org/novaforge/forge/error/error.get.json.ftl" >
</#if>