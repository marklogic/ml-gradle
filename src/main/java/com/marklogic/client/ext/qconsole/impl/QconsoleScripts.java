package com.marklogic.client.ext.qconsole.impl;

/**
 * This scripts are defined as strings so that it's easy to reuse this in an environment like Gradle without
 * having to read files from the classpath.
 */
public class QconsoleScripts {

	public static final String IMPORT = "xquery version \"1.0-ml\";\n" +
		"\n" +
		"declare namespace qconsole=\"http://marklogic.com/appservices/qconsole\";\n" +
		"\n" +
		"import module namespace amped-qconsole = \"http://marklogic.com/appservices/qconsole/util-amped\" at \"/MarkLogic/appservices/qconsole/qconsole-amped.xqy\";\n" +
		"import module namespace idecl = \"http://marklogic.com/appservices/qconsole/decl\" at \"/MarkLogic/appservices/qconsole/qconsole-decl.xqy\";\n" +
		"import module namespace qconsole-model = \"http://marklogic.com/appservices/qconsole/model\" at \"/MarkLogic/appservices/qconsole/qconsole-model.xqy\";\n" +
		"\n" +
		"declare namespace eval = \"xdmp:eval\";\n" +
		"\n" +
		"declare variable $exported-workspace as node() external;\n" +
		"declare variable $user as xs:string external;\n" +
		"\n" +
		"declare function local:qconsole-eval(\n" +
		"    $xquery as xs:string,\n" +
		"    $vars as item()*,\n" +
		"    $options as element(eval:options)?\n" +
		") as item()*\n" +
		"{   xdmp:log(text{(\"local:qconsole-eval\", xdmp:quote($vars))}),\n" +
		"    xdmp:security-assert(\"http://marklogic.com/xdmp/privileges/qconsole\", \"execute\"),\n" +
		"    xdmp:eval($xquery, $vars, <options xmlns=\"xdmp:eval\">\n" +
		"            <database>{xdmp:database(\"App-Services\")}</database>\n" +
		"          </options>)\n" +
		"};\n" +
		"\n" +
		"declare function local:import-workspace(\n" +
		"    $workspace as element(),\n" +
		"    $user as xs:string \n" +
		") as xs:string*\n" +
		"{\n" +
		"    let $eval-query :=\n" +
		"       'declare namespace qconsole = \"http://marklogic.com/appservices/qconsole\";\n" +
		"        import module namespace qconsole-model=\"http://marklogic.com/appservices/qconsole/model\"\n" +
		"            at \"/MarkLogic/appservices/qconsole/qconsole-model.xqy\";\n" +
		"        import module namespace amped-qconsole = \"http://marklogic.com/appservices/qconsole/util-amped\"\n" +
		"            at \"/MarkLogic/appservices/qconsole/qconsole-amped.xqy\";\n" +
		"        declare variable $xquery-query-template as xs:string external;\n" +
		"        declare variable $workspace as element(export) external;\n" +
		"        declare variable $user as xs:string external;\n" +
		"        let $_ := xdmp:log(text{(\"user\", $user)})\n" +
		"        let $wsid := xdmp:random()\n" +
		"        let $imported-wsname := string($workspace/workspace/@name)\n" +
		"        let $existing-wsnames := amped-qconsole:qconsole-get-user-workspaces(())/qconsole:name/string()\n" +
		"        let $wsname :=\n" +
		"            if( $imported-wsname = $existing-wsnames )\n" +
		"            then qconsole-model:generate-workspace-name(())\n" +
		"            else $imported-wsname\n" +
		"        let $queries := $workspace/workspace/query\n" +
		"        let $userid := xdmp:user($user)\n" +
		"        let $_ := xdmp:log(text{(\"userid\", $userid)})\n" +
		"        let $ws :=  <qconsole:workspace>\n" +
		"                        <qconsole:id>{$wsid}</qconsole:id>\n" +
		"                        <qconsole:name>{$wsname}</qconsole:name>\n" +
		"                        <qconsole:security>\n" +
		"                            <qconsole:userid>{$userid}</qconsole:userid>\n" +
		"                        </qconsole:security>\n" +
		"                        <qconsole:active>true</qconsole:active>\n" +
		"                        <qconsole:queries>\n" +
		"                            {\n" +
		"                            for $q at $i in $queries\n" +
		"                            let $qid := xdmp:random()\n" +
		"                            let $qname := string($q/@name)\n" +
		"                            let $focus := string($q/@focus)\n" +
		"                            let $active := string($q/@active)\n" +
		"                            let $content-source :=\n" +
		"                                if ( exists($q/@content-source) )\n" +
		"                                then string($q/@content-source)\n" +
		"                                else qconsole-model:default-content-source()\n" +
		"                            let $mode := string($q/@mode)\n" +
		"                            let $query-text := text { $q }\n" +
		"                            let $q-uri := concat(\"/queries/\", $qid, \".txt\")\n" +
		"                            let $save-q := amped-qconsole:qconsole-document-insert($q-uri, $query-text)\n" +
		"                            return\n" +
		"                            <qconsole:query>\n" +
		"                                <qconsole:id>{$qid}</qconsole:id>\n" +
		"                                <qconsole:name>{$qname}</qconsole:name>\n" +
		"                                <qconsole:content-source>{$content-source}</qconsole:content-source>\n" +
		"                                <qconsole:active>{$active}</qconsole:active>\n" +
		"                                <qconsole:focus>{$focus}</qconsole:focus>\n" +
		"                                <qconsole:mode>{$mode}</qconsole:mode>\n" +
		"                            </qconsole:query>\n" +
		"                            }\n" +
		"                        </qconsole:queries>\n" +
		"                    </qconsole:workspace>\n" +
		"        let $ws-uri := concat(\"/workspaces/\", $wsid, \".xml\")\n" +
		"        let $save-ws := amped-qconsole:qconsole-document-insert($ws-uri, $ws)\n" +
		"        let $set-active := qconsole-model:set-only-one-workspace-active($wsid)\n" +
		"        return $wsid'\n" +
		"            \n" +
		"    let $new-wsid := \n" +
		"        local:qconsole-eval($eval-query, \n" +
		"            (xs:QName(\"workspace\"), $workspace, \n" +
		"            xs:QName(\"xquery-query-template\"), $idecl:default-query-text,\n" +
		"            xs:QName(\"user\"), $user), ())\n" +
		"    let $ws-uri := concat(\"/workspaces/\", $new-wsid, \".xml\")\n" +
		"    return $ws-uri\n" +
		"};\n" +
		"\n" +
		"local:import-workspace($exported-workspace/element(), $user)";

	public final static String EXPORT = "xquery version \"1.0-ml\";\n" +
		"\n" +
		"declare namespace qconsole=\"http://marklogic.com/appservices/qconsole\";\n" +
		"\n" +
		"declare variable $user as xs:string external;\n" +
		"declare variable $workspace as xs:string external;\n" +
		"\n" +
		"declare function local:do-eval($query as xs:string, $vars) {\n" +
		"  xdmp:eval($query, $vars, \n" +
		"      <options xmlns=\"xdmp:eval\">\n" +
		"      <database>{xdmp:database(\"App-Services\")}</database>\n" +
		"      </options>)\n" +
		"};\n" +
		"\n" +
		"declare function local:get-ws-uri($user as xs:string, $workspace as xs:string) {\n" +
		"  let $ws-query := 'xquery version \"1.0-ml\";\n" +
		"    declare namespace qconsole = \"http://marklogic.com/appservices/qconsole\";\n" +
		"    declare variable $user as xs:string external;\n" +
		"    declare variable $workspace as xs:string external;\n" +
		"    cts:uris((), (), cts:and-query((\n" +
		"        cts:directory-query(\"/workspaces/\"),\n" +
		"        cts:element-value-query(xs:QName(\"qconsole:userid\"), xs:string(xdmp:user($user))),\n" +
		"        cts:element-value-query(xs:QName(\"qconsole:name\"), $workspace)\n" +
		"    ))\n" +
		"    )'\n" +
		"  return local:do-eval($ws-query, (xs:QName(\"user\"), $user, xs:QName(\"workspace\"), $workspace))\n" +
		"};\n" +
		"\n" +
		"declare function local:get-workspace($ws-uri as xs:string) {\n" +
		"  let $query := \"declare variable $ws-uri as xs:string external; fn:doc($ws-uri)\"\n" +
		"  return local:do-eval($query, (xs:QName(\"ws-uri\"), $ws-uri))\n" +
		"};\n" +
		"\n" +
		"let $user := ($user, xdmp:get-current-user())[1]\n" +
		"\n" +
		"let $ws-uri := local:get-ws-uri($user, $workspace)\n" +
		"let $ws := local:get-workspace($ws-uri)\n" +
		"let $queries := \n" +
		"    for $q in $ws/qconsole:workspace/qconsole:queries/qconsole:query\n" +
		"    return \n" +
		"      <query name=\"{string($q/qconsole:name)}\" focus=\"{string($q/qconsole:focus)}\" active=\"{string($q/qconsole:active)}\" mode=\"{string($q/qconsole:mode)}\">\n" +
		"        {local:do-eval(concat(\"fn:doc('/queries/\", xs:unsignedLong($q/qconsole:id), \".txt')\"), ())}\n" +
		"      </query>\n" +
		"\n" +
		"let $export := \n" +
		"    if ($queries) then (\n" +
		"    <export>\n" +
		"      <workspace name=\"{string($ws/qconsole:workspace/qconsole:name)}\">\n" +
		"        {$queries}\n" +
		"      </workspace>\n" +
		"    </export> )\n" +
		"    else (text{\"No workspace found with the name of \", $workspace, \".\"})\n" +
		"\n" +
		"return $export";

}
