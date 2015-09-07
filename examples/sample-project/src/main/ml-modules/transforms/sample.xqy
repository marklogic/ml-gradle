xquery version "1.0-ml";

module namespace transform = "http://marklogic.com/rest-api/transform/sample";

declare function transform(
  $context as map:map,
  $params as map:map,
  $content as document-node()
  ) as document-node()
{
  xdmp:log("Hello from sample transform! Normally, you'd do something interesting here besides just returning the content node"),
  
  $content
};