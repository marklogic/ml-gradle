package sample;

import static com.jayway.restassured.RestAssured.basic;
import static com.jayway.restassured.RestAssured.get;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.marklogic.clientutil.DatabaseClientConfig;
import com.marklogic.junit.Fragment;

/**
 * This test shows an example of using RestAssured instead of the MarkLogic Java API to invoke MarkLogic REST API
 * endpoints. The key is that we can easily configure RestAssured by reusing the configuration object retrieved from the
 * Spring container.
 */
public class SearchDocumentsViaRestAssuredTest extends AbstractSampleProjectTest {

    @Before
    public void initializeRestAssured() {
        logger.info("Initializing RestAssured...");

        DatabaseClientConfig config = getApplicationContext().getBean(DatabaseClientConfig.class);
        RestAssured.baseURI = "http://" + config.getHost();
        RestAssured.port = config.getPort();
        RestAssured.authentication = basic(config.getUsername(), config.getPassword());

        logger.info("RestAssured URI: " + RestAssured.baseURI);
        logger.info("RestAssured port: " + RestAssured.port);
    }

    @Test
    public void twoDocuments() {
        loadPerson("/jane.xml", "Jane", "This is a sample document");
        loadPerson("/john.xml", "John", "This is another sample document");

        /**
         * This shows a simple RestAssured GET call that will return XML, and we can easily parse that to get a Fragment
         * instance that we can use for making assertions.
         */
        Fragment results = parse(get("/v1/search").asString());
        results.assertElementExists("/search:response/search:result[@uri = '/jane.xml']");
        results.assertElementExists("/search:response/search:result[@uri = '/john.xml']");

        /**
         * This shows searching for a term and then asserting on the total and snippet highlighting in the result.
         */
        results = parse(get("/v1/search?q=Jane").asString());
        results.assertElementExists("/search:response[@total = '1']/search:result[@uri = '/jane.xml']/search:snippet/search:match/search:highlight[. = 'Jane']");

        /**
         * RestAssured also makes it easy to assert on JSON responses.
         */
        JsonPath json = get("/v1/search?q=John&format=json").jsonPath();
        assertEquals(1, json.getInt("total"));
        assertEquals("/john.xml", json.get("results[0].uri"));
    }
}
