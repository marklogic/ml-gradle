xquery version "1.0-ml";
import module namespace rdt = "http://marklogic.com/xdmp/redaction" at "/MarkLogic/redaction.xqy";


declare variable $URI external;

let $collections :=
	xdmp:invoke-function(
		function() { xdmp:document-get-collections($URI) },
		<options xmlns="xdmp:eval">
			<database>{xdmp:database("redaction-project-schemas")}</database>
		</options>)


return rdt:rule-validate(($collections))


