xquery version "1.0-ml";

module namespace sample = "urn:sampleapp";

declare function echo($str as xs:string?) as xs:string
{
  "You said: " || $str
};
