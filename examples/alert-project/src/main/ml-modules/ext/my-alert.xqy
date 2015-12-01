xquery version "1.0-ml";

declare namespace alert = "http://marklogic.com/xdmp/alert";

import module "http://marklogic.com/xdmp/alert" at "/MarkLogic/alert.xqy";

declare variable $alert:config-uri as xs:string external;
declare variable $alert:doc as node() external;
declare variable $alert:rule as element(alert:rule) external;
declare variable $alert:action as element(alert:action) external;

xdmp:log("In my-alert.xqy, URI: " || xdmp:node-uri($alert:doc))
