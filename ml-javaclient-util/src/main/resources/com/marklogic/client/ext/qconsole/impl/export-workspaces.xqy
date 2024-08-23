xquery version "1.0-ml";

declare namespace qconsole="http://marklogic.com/appservices/qconsole";

declare variable $user as xs:string external;
declare variable $workspace as xs:string external;

declare function local:do-invoke($function as xdmp:function) {
	xdmp:invoke-function($function,
		<options xmlns="xdmp:eval">
			<database>{xdmp:database("App-Services")}</database>
		</options>)
};

declare function local:get-workspace-uri($user as xs:string, $workspace as xs:string) {
	let $workspace-query := function() {
		cts:uris((), (), cts:and-query((
			cts:directory-query("/workspaces/"),
			cts:element-value-query(xs:QName("qconsole:userid"), xs:string(xdmp:user($user))),
			cts:element-value-query(xs:QName("qconsole:name"), $workspace)
		)))
	}
	return local:do-invoke($workspace-query)
};

declare function local:get-workspace($ws-uri as xs:string) {
	let $query := function() { fn:doc($ws-uri) }
	return local:do-invoke($query)
};

declare function local:to-attribute($element as element()) {
	attribute { $element/local-name() } { string($element) }
};

let $user := ($user, xdmp:get-current-user())[1]
let $workspace-uri := local:get-workspace-uri($user, $workspace)
let $workspace := local:get-workspace($workspace-uri)
let $queries :=
	for $query in $workspace/qconsole:workspace/qconsole:queries/qconsole:query
	return
		<query>{
        $query/qconsole:* ! local:to-attribute(.),
			  local:do-invoke(function() {fn:doc("/queries/" || xs:string($query/qconsole:id) || ".txt")})
		}</query>
return
	if ($queries)
	then
		<export>
			<workspace name="{string($workspace/qconsole:workspace/qconsole:name)}">
				{$queries}
			</workspace>
		</export>
	else
		text{ "No workspace found with the name of ", $workspace, "." }
