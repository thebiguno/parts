{"data": [
<#list parts as part>
{
	"description": <#attempt>${part.findAttribute("Description").getValue()?html}<#recover>-</#attempt>
}
</#list>
]}