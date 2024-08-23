xquery version "1.0-ml";

module namespace resource = "http://marklogic.com/rest-api/resource/sample";

declare function get(
	$context as map:map,
	$params as map:map
) as document-node()*
{
	xdmp:log("%%REPLACEME%% called")
};
