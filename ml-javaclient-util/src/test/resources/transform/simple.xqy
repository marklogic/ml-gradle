xquery version "1.0-ml";

module namespace x = "http://marklogic.com/rest-api/transform/simple";

declare function x:transform(
  $context as map:map,
  $params as map:map,
  $content as document-node())
as document-node() {
  document {
    element { fn:local-name($content/element()) } {
      $content/element(),
      element transform {},
      for $param in map:keys($params)
      return element { $param } { map:get($params, $param) }
    }
  }

};
