xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace sec = "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy";

declare function local:find-appservers($config, $app-name)
{
  for $id in admin:get-appserver-ids($config)
  let $name := admin:appserver-get-name($config, $id)
  where $name = $app-name or fn:starts-with($name, fn:concat($app-name, "-"))
  return $id
};

(:
Switching the app servers to the Documents database first (necessary so we can delete the underlying databases and 
forests before we delete the XDBC server we talk to) seems to remove the possibility of the content forest not being
deleted (see the comment below).
:)

let $app-name := "%%APP_NAME%%" 
let $config := admin:get-configuration()

let $appserver-ids := local:find-appservers($config, $app-name)

let $documents-id := admin:database-get-id($config, "Documents")

let $_ :=
  for $id in $appserver-ids
  let $config := admin:get-configuration()
  let $config := admin:appserver-set-database($config, $id, $documents-id)
  let $config := admin:appserver-set-modules-database($config, $id, $documents-id)
  return admin:save-configuration($config)

return ()
;



xquery version "1.0-ml";

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace sec = "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy";

declare function local:find-appservers($config, $app-name)
{
  for $id in admin:get-appserver-ids($config)
  let $name := admin:appserver-get-name($config, $id)
  where $name = $app-name or fn:starts-with($name, fn:concat($app-name, "-"))
  return $id
};

declare function local:find-databases($config, $app-name)
{
  for $name in (fn:concat($app-name, "-content"), fn:concat($app-name, "-modules"), fn:concat($app-name, "-test-content"), fn:concat($app-name, "-triggers"))
  where admin:database-exists($config, $name)
  return admin:database-get-id($config, $name)
};

declare function local:find-forests($config, $app-name, $database-ids)
{
  for $forest-id in admin:get-forest-ids($config)
  let $name := admin:forest-get-name($config, $forest-id)
  where admin:forest-get-database($config, $forest-id) = $database-ids
  return $forest-id
};

declare function local:delete-forests($config, $forest-ids)
{
  for $id in $forest-ids 
  let $db-id := admin:forest-get-database($config, $id) 
  let $config := admin:get-configuration() 
  let $config := admin:database-detach-forest($config, $db-id, $id) 
  let $_ := admin:save-configuration($config) 
  let $config := admin:get-configuration() 
  let $message := text{"Deleting forest", admin:forest-get-name($config, $id)} 
  let $_ := xdmp:log($message) 
  let $config := admin:forest-delete($config, $id, fn:true()) 
  return (admin:save-configuration($config), $message) 
};

declare function local:delete-databases($config, $database-ids)
{
  for $id in $database-ids 
  let $config := admin:get-configuration() 
  let $message := text{"Deleting database", admin:database-get-name($config, $id)} 
  let $_ := xdmp:log($message) 
  let $config := admin:database-delete($config, $id) 
  return (admin:save-configuration($config), $message)
};

declare function local:delete-appservers($config, $appserver-ids)
{  
  for $id at $index in $appserver-ids 
  let $config := admin:get-configuration() 
  let $message := text{"Deleting appserver", admin:appserver-get-name($config, $id)} 
  let $_ := xdmp:log($message) 
  let $config := admin:appserver-delete($config, $id) 
  return 
    if ($index = fn:count($appserver-ids)) then
      let $_ := admin:save-configuration-without-restart($config)
      return $message 
    else (admin:save-configuration($config), $message)
};
    
let $app-name := "%%APP_NAME%%" 
let $config := admin:get-configuration()

let $appserver-ids := local:find-appservers($config, $app-name)
let $database-ids := local:find-databases($config, $app-name)
let $forest-ids := local:find-forests($config, $app-name, $database-ids)

let $database-names := 
  for $id in $database-ids
  return admin:database-get-name($config, $id)
let $_ := xdmp:log(text{"Found database names", $database-names})

let $forest-names := 
  for $id in $forest-ids
  return admin:forest-get-name($config, $id)
let $_ := xdmp:log(text{"Found forest names", $forest-names})

(:
The content forest is consistently not being found, but it's still deleted. The logs show the forest somehow being
deleted during the find-forests call. There's also a warning logged: "Warning: XDMP-FORESTNOT: Forest 
(app name)-content-1 not available: unmounted". Right after that, the forest is deleted. But again, this oddly occurs 
during the find-forests function call, which means it's not returned as a deleted forest in the delete-forests call 
below. Very odd - seems like a bug. So the text returned by this module won't list the content database as having
been deleted, but it in fact has been. 
:)

let $forest-messages := local:delete-forests($config, $forest-ids)
let $database-messages := local:delete-databases($config, $database-ids) 
let $appserver-messages := local:delete-appservers($config, $appserver-ids)

return (
  text{"Found databases", $database-names}, 
  text{"Found forests", $forest-names}, 
  $database-messages, 
  $forest-messages, 
  $appserver-messages
)

