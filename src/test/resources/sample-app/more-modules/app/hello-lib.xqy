xquery version "1.0-ml";

module namespace hello = "urn:sampleapp:hello";

declare function hello($str as xs:string?) as xs:string
{
  "Hello: " || $str
};
