<#if !error??>
{
	"user":  
	{
		"userName" : "${userName}",
		"message" : "${message}"
	}
}
<#else>
<#include "/org/novaforge/forge/error/error.post.json.ftl" >
</#if>