xquery version "1.0-ml";

(:
Sample query that returns all URIs.
:)

let $uris := cts:uris((), (), cts:and-query(()))

return (count($uris), $uris)