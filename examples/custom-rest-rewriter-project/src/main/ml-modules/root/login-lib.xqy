xquery version "1.0-ml";

(:
This is implemented in XQuery instead of SJS so that it's easier to import via XQuery or SJS.

This is an example implementation - customize as needed.
:)

module namespace login = "org:example";

declare function login-noauth-user() as empty-sequence()
{
	let $user-header := xdmp:get-request-header("X-my-marklogic-role")
	let $_ := xdmp:login(xdmp:get-current-user(), (), fn:false(), $user-header)
	return ()
};

