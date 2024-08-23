xquery version "1.0-ml";

module namespace service = "http://marklogic.com/rest-api/resource/sql";

declare function get(
  $context as map:map,
  $params  as map:map
  ) as document-node()*
{
  document {
    xdmp:sql(
      map:get($params, "query"),
      "array"
    )
  }
};
