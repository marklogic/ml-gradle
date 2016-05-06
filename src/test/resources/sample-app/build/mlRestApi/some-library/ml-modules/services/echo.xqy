xquery version "1.0-ml";

module namespace resource = "http://marklogic.com/rest-api/resource/echo";

import module namespace sample = "urn:sampleapp" at "/ext/some-lib.xqy";

declare function get(
  $context as map:map,
  $params  as map:map
  ) as document-node()*
{
  sample:foo(),
  ()
};
