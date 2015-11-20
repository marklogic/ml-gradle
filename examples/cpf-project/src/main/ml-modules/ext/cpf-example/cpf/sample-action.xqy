xquery version "1.0-ml";

import module namespace cpf = "http://marklogic.com/cpf" at "/MarkLogic/cpf/cpf.xqy";

declare variable $cpf:document-uri as xs:string external;
declare variable $cpf:transition as node() external;

if (cpf:check-transition($cpf:document-uri,$cpf:transition)) then try {
  
  
  xdmp:log("In sample-action.xqy, URI: " || $cpf:document-uri),
  
  let $doc := doc($cpf:document-uri)/element()
  return xdmp:node-insert-child($doc, <hello>World {fn:current-dateTime()}</hello>),
  
  
  xdmp:document-set-property($cpf:document-uri, <sample-prop xmlns='http://marklogic.com/sample'>Hello from the CPF action</sample-prop>),
  
  
  
  
  cpf:success($cpf:document-uri, $cpf:transition, ())
} 

catch ($e) {
  xdmp:log($e),
  cpf:failure($cpf:document-uri, $cpf:transition, $e, ())
}

else ()