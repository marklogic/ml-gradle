module namespace example = "example";

declare function echo($str as xs:string) as xs:string
{
	let $_ := ()
	return "You said: " || $str
};

declare function not-covered() as xs:string
{
	let $_ := ()
  return "This function is not expected to be covered by the test suite"
};

declare function also-not-covered() as empty-sequence()
{
	()
};
