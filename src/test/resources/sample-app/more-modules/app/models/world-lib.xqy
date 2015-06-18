xquery version "1.0-ml";

module namespace world = "urn:sampleapp:world";

declare function world($str as xs:string?) as xs:string
{
  "World: " || $str
};
