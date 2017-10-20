xquery version "1.0-ml";

let $uris :=
	xdmp:invoke-function(
		function() { cts:uris((), (), cts:directory-query("/redactionRules/")) },
		<options xmlns="xdmp:eval">
			<database>{xdmp:database("redaction-project-schemas")}</database>
		</options>)

return (count($uris), $uris)
