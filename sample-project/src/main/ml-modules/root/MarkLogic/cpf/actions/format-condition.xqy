xquery version "1.0-ml";

(:
This is just a sample of how to override the built-in MarkLogic modules by placing modules in the ml-modules/root
directory.
:)

declare namespace cpf = "http://marklogic.com/cpf";

declare default collation "http://marklogic.com/collation/codepoint";

declare variable $cpf:document-uri as xs:string external;

xdmp:uri-format($cpf:document-uri = ("text", "xml"))
