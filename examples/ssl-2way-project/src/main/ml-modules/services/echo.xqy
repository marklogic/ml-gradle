xquery version "1.0-ml";

module namespace service = "http://marklogic.com/rest-api/resource/echo";

declare function get(
  $context as map:map,
  $params  as map:map
  ) as document-node()*
{
  document {
    text {
      "Echo 7 - You said:",
      map:get($params, "text")
    }
  }
};
