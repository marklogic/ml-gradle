module namespace example = "example";

declare function echo($str as xs:string) as xs:string
{
	"You said: " || $str
};
