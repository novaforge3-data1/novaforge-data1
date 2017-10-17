<#if !error??>
{
	"project":  
	{
		"roles" : "${roles}",
		"message" : "${message}"
	}
}
<#else>
<#include "/org/novaforge/forge/error/error.get.json.ftl" >
</#if>