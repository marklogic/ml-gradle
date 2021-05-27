xquery version "1.0-ml";

module namespace sample = "urn:sampleapp";

declare function echo($str as xs:string?) as xs:string
{
  "You said: " || $str
};

declare function get-host-status()
{
	xdmp:host-status(xdmp:host())
};
