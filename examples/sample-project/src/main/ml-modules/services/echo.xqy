xquery version "1.0-ml";

module namespace service = "http://marklogic.com/rest-api/resource/echo";

import module namespace sample = "urn:sampleapp" at "/ext/sample-project/lib/sample-lib.xqy";

declare function get(
  $context as map:map,
  $params  as map:map
  ) as document-node()*
{
  document {
    text {
      sample:echo(map:get($params, "text"))
    }
  }
};
