xquery version "1.0-ml";

module namespace other = "urn:sampleapp:other";

declare function hello($str as xs:string?) as xs:string
{
  "Hello: " || $str
};
