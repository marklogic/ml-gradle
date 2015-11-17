xquery version "1.0-ml";

declare namespace cpf = "http://marklogic.com/cpf";

declare default collation "http://marklogic.com/collation/codepoint";

declare variable $cpf:document-uri as xs:string external;

let $_ := xdmp:log("In sample condition: " || $cpf:document-uri)

return true()
