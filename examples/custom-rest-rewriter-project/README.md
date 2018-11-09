This project shows one approach for when you'd like to use the MarkLogic REST API for an app server, but you need
to do some pre-processing on every request. A common need is to use application layer security for the app server
with a default user that has minimum privileges, and then perform an xdmp:login call based on e.g. the value of an 
HTTP header. That approach is shown in this example project, but you can apply this approach for generating a custom
rewriter and REST modules to perform any kind of pre-processing that you need. 

## Generating the custom rewriter modules

To try this out locally, first run the custom Gradle task for generating a copy of every REST API dispatch modules and
a custom REST XML rewriter that invokes each of the dispatch modules:

    ./gradlew generateCustomRewriterModules
    
This will write several dozen modules to src/main/ml-modules/root/custom-rest-rewriter (which is gitignore'd so that you
can generate these yourself). If you look at one of the modules, you'll see the following code:

    xquery version '1.0-ml';
    import module namespace login = 'org:example' at '/login-lib.xqy';
    login:login-noauth-user(),
    xdmp:invoke('/MarkLogic/rest-api/default.xqy')

In this example, every module has a call to "login:login-noauth-user", which handles making an xdmp:login call based
on the value of an HTTP header, and then invokes the appropriate MarkLogic REST module to handle the request. 

These modules are generated via 3 files in the root of this directory:

- rewriter-original.xml = a copy of the default REST XML rewriter, as of MarkLogic 9.0-7
- rewriter-modify-dispatches.xsl = an XSL transform that modifies the value of each dispatch element in the rewriter
- rewriter-extract-dispatches.xsl = an XSL transform that returns the value of every dispatch element in a simple XML
structure that is easy to manipulate within Groovy

You are free to store these files wherever you want, they don't have to be in the root directory. Just be sure to 
update the Gradle task to account for any changes you make. 


## Deploy the application

Next, deploy the application:

     ./gradlew -i mlDeploy

The application deploys two REST servers - a standard one on port 8228 that uses digest authentication, and then a
"no authentication" one on port 8229 that uses application layer authentication and the custom rewriter. 

## Test the application

To test the difference between the two servers, try a simple request to the search endpoint on 8228 using your admin user
(change the admin password as needed):

     curl --anyauth --user admin:admin http://localhost:8228/v1/search
  
This will work fine, you'll get an XML search response back. But if you try the same request on port 8229:

    curl --anyauth --user admin:admin http://localhost:8229/v1/search

You'll get an error stating "You do not have permission to this method and URL.". That's because the admin user/password
are ignored with application level security, and the default user - "custom-rest-rewriter-user" - isn't getting any
roles via the xdmp:login that give it permission. 

We can fix this by specifying the header that login-lib.xqy is looking for (and there's no point in specifying a user 
because it'll be ignored in favor of the default user):

    curl -H "X-my-marklogic-role: rest-admin" http://localhost:8229/v1/search

The value of the header - "rest-admin" - will be added as a role to the request, thus allowing the search request to 
succeed. 

Note that this is just a demonstration of how to generate a custom XML rewriter with a set of custom endpoints that 
call a function performing any logic that you wish. For this particular example, you'd want to ensure that no system
could access the "noauth" REST server except those known to have already authenticated a user. But your function wouldn't
have to call xdmp:login - it could perform some custom logging, or do any sort of pre-processing that you wish. The key
is that this project shows an easy to insert that pre-processing into an XML rewriter. 

After you finish testing this out, you can get rid of the app:

    ./gradlew -Pconfirm=true -i mlUndeploy
