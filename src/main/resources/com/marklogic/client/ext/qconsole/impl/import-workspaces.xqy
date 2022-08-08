xquery version "1.0-ml";
(:
  This relies on internal APIs in the qconsole-model function that have changed between MarkLogic 8 and 9.
  Specifically, qconsole-model:default-content-source() in ML8 was removed and qconsole-model:default-database()
  can be used instead.
:)
declare namespace qconsole = "http://marklogic.com/appservices/qconsole";

import module namespace amped-qconsole = "http://marklogic.com/appservices/qconsole/util-amped" at "/MarkLogic/appservices/qconsole/qconsole-amped.xqy";
import module namespace qconsole-model = "http://marklogic.com/appservices/qconsole/model" at "/MarkLogic/appservices/qconsole/qconsole-model.xqy";

declare variable $exported-workspace as node() external;
declare variable $user as xs:string external;

declare function local:qconsole-invoke(
	$xquery as xdmp:function
) as item()*
{
	xdmp:security-assert("http://marklogic.com/xdmp/privileges/qconsole", "execute"),
	xdmp:invoke-function($xquery,
		<options xmlns="xdmp:eval">
			<database>{xdmp:database("App-Services")}</database>
		</options>)
};

declare function local:workspace-uri($workspace-id) {
	"/workspaces/" || xs:string($workspace-id) || ".xml"
};

declare function local:import-workspace(
	$workspace as element(),
	$user as xs:string
) as xs:string*
{
  let $query := function() {
    let $_ := xdmp:log(text{("user", $user)})
    let $workspace-id := xdmp:random()
    let $imported-workspace-name := string($workspace/workspace/@name)
    let $existing-workspace-names := amped-qconsole:qconsole-get-user-workspaces(())/qconsole:name/string()
    let $workspace-name :=
      if ($imported-workspace-name = $existing-workspace-names)
      then qconsole-model:generate-workspace-name(())
      else $imported-workspace-name
    let $queries := $workspace/workspace/query
    let $user-id := xdmp:user($user)
    let $_ := xdmp:log(text{("userid", $user-id)})
    let $workspace :=
		  <qconsole:workspace>
			  <qconsole:id>{$workspace-id}</qconsole:id>
				<qconsole:name>{$workspace-name}</qconsole:name>
				<qconsole:security>
				  <qconsole:userid>{$user-id}</qconsole:userid>
				</qconsole:security>
				<qconsole:active>true</qconsole:active>
				<qconsole:queries>{
					for $query in $queries
					let $source :=
						if (exists($query/@content-source))
						then qconsole-model:convert-content-source($query/@content-source)
						else
							let $s := map:map()
							let $database-name :=
								if (exists($query/@database-name) and string-length($query/@database-name) gt 0)
								then $query/@database-name ! (., map:put($s, ./local-name(), .))
								else ""
							let $database :=
								let $db-id :=
									if ( (exists($query/@database) and string-length($query/@database) gt 0) or string-length($database-name) gt 0 )
									then qconsole-model:identify-resource("database", $query/@database, $database-name)
									else qconsole-model:default-database()
								let $_ := map:put($s, "database", $db-id)
								return $db-id
							let $server-name :=
								if (exists($query/@server-name) and string-length($query/@server-name) gt 0)
								then $query/@server-name ! (., map:put($s, ./local-name(), .))
								else ""
							let $server :=
								let $server-id :=
									if ( (exists($query/@server) and string-length($query/@server) gt 0) or string-length($server-name) gt 0 )
									then qconsole-model:identify-resource("server", $query/@server, $server-name)
									else qconsole-model:default-app-server($database)
								let $_ := map:put($s, "server", $server-id)
								return $server-id
							return $s
					let $_ :=
					  for $item in ($query/@*[not(local-name() = ("id", "database", "database-name", "server", "server-name"))])
			      return map:put($source, $item/local-name(), $item)
					let $query-text := text {$query}
					let $query-id := xdmp:random()
					let $query-uri := concat("/queries/", $query-id, ".txt")
					let $save-query := amped-qconsole:qconsole-document-insert($query-uri, $query-text)
					return
						<qconsole:query>
							<qconsole:id>{$query-id}</qconsole:id>
							{ map:keys($source) ! element {"qconsole:" || .} { string(map:get($source, .)) } }
						</qconsole:query>
				}</qconsole:queries>
			</qconsole:workspace>
    let $workspace-uri := local:workspace-uri($workspace-id)
    let $save-workspace := amped-qconsole:qconsole-document-insert($workspace-uri, $workspace)
    let $set-active := qconsole-model:set-only-one-workspace-active($workspace-id)
    return $workspace-id
	}
	let $new-wsid := local:qconsole-invoke($query)
	let $workspace-uri := local:workspace-uri($new-wsid)
	return $workspace-uri
};

local:import-workspace($exported-workspace/element(), $user)
