import module namespace example = "example" at "/example-lib.xqy";
import module namespace test = "http://marklogic.com/roxy/test-helper" at "/test/test-helper.xqy";

test:assert-equal("You said: hello", example:echo("hello"))
