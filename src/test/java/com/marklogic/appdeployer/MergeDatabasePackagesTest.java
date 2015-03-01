package com.marklogic.appdeployer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Namespace;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.ml7.Ml7AppDeployer;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.MarkLogicNamespaceProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.XmlHelper;

public class MergeDatabasePackagesTest extends XmlHelper {

    @Test
    public void mergePackages() throws IOException {
        List<String> mergeFilePaths = new ArrayList<>();
        mergeFilePaths.add("src/test/resources/test-content-database.xml");
        mergeFilePaths.add("src/test/resources/test-content-database2.xml");

        AppConfig config = new AppConfig();
        config.setDatabasePackageFilePaths(mergeFilePaths);
        Ml7AppDeployer sut = new Ml7AppDeployer(null);

        sut.mergeDatabasePackages(config);

        String xml = new String(FileCopyUtils.copyToByteArray(new File(config.getContentDatabaseFilePath())));
        Fragment db = parse(xml);

        db.assertElementExists("/db:package-database");

        // Assert on simple elements
        db.assertElementExists("//db:word-positions[. = 'true']");
        db.assertElementExists("//db:collection-lexicon[. = 'true']");

        // Assert on complex elements
        String indexPath = "/db:package-database/db:config/db:package-database-properties/db:range-element-indexes/db:range-element-index";
        db.assertElementExists(indexPath
                + "[db:scalar-type = 'dateTime' and db:namespace-uri = 'http://marklogic.com/test' and db:localname = 'testDateTime']");
        db.assertElementExists(indexPath
                + "[db:scalar-type = 'dateTime' and db:namespace-uri = 'http://marklogic.com/test' and db:localname = 'secondTestDateTime']");

        assertGeospatialIndexesExist(db);

        db.assertElementExists("/db:package-database/db:config/db:links/db:schema-database[. = 'my-schemas-database']");
        db.assertElementExists("/db:package-database/db:config/db:links/db:security-database[. = 'my-security-database']");
        db.assertElementExists("/db:package-database/db:config/db:links/db:triggers-database[. = 'my-triggers-database']");

        db.assertElementExists("/db:package-database/db:config/db:package-database-properties/db:word-lexicons/db:word-lexicon[. = 'http://marklogic.com/collation/codepoint']");

        db.assertElementExists("//db:range-field-indexes/db:range-field-index[db:scalar-type = 'int' and db:field-name = 'intField']");
        db.assertElementExists("//db:fields/db:field[db:field-name = 'intField' and db:field-path[db:path = '/some/int/element' and db:weight = '1']]");

        db.assertElementExists("//db:range-field-indexes/db:range-field-index[db:scalar-type = 'string' and db:field-name = 'secondField']");
        db.assertElementExists("//db:fields/db:field[db:field-name = 'secondField' and db:field-path[db:path = '/some/other/element' and db:weight = '1']]");

        // Ensuring word query settings are covered as a field with no field name
        db.assertElementExists("//db:fields/db:field[db:field-name = '' and db:include-root = 'true' and "
                + "db:included-elements/db:included-element[db:namespace-uri = 'urn:sample'] and "
                + "db:excluded-elements/db:excluded-element[db:namespace-uri = 'urn:sample']]");

        db.assertElementExists("//db:element-word-lexicons/db:element-word-lexicon[db:namespace-uri = 'urn:some:namespace' "
                + "and db:localname = 'SomeElement' and db:collation = 'http://marklogic.com/collation/codepoint']");
        db.assertElementExists("//db:element-word-lexicons/db:element-word-lexicon[db:namespace-uri = 'urn:some:namespace2' "
                + "and db:localname = 'AnotherElement' and db:collation = 'http://marklogic.com/collation/codepoint']");

        db.assertElementExists("//db:element-word-query-throughs/db:element-word-query-through[db:namespace-uri = 'urn:first:namespace' and db:localname = 'FirstElement']");
        db.assertElementExists("//db:element-word-query-throughs/db:element-word-query-through[db:namespace-uri = 'urn:second:namespace' and db:localname = 'SecondElement']");

        // db.prettyPrint();
    }

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new MarkLogicNamespaceProvider() {
            @Override
            protected List<Namespace> buildListOfNamespaces() {
                List<Namespace> list = super.buildListOfNamespaces();
                list.add(Namespace.getNamespace("db", "http://marklogic.com/manage/package/databases"));
                return list;
            }

        };
    }

    /**
     * Not asserting on each element yet, just making sure at least one child element with text exists.
     */
    private void assertGeospatialIndexesExist(Fragment db) {
        db.assertElementExists("//db:geospatial-element-indexes/db:geospatial-element-index[db:localname = 'testRegion']");
        db.assertElementExists("//db:geospatial-element-indexes/db:geospatial-element-index[db:localname = 'theelement']");
        db.assertElementExists("//db:geospatial-element-child-indexes/db:geospatial-element-child-index[db:localname = 'childname']");
        db.assertElementExists("//db:geospatial-element-pair-indexes/db:geospatial-element-pair-index[db:parent-localname = 'parentname']");
        db.assertElementExists("//db:geospatial-element-attribute-pair-indexes/db:geospatial-element-attribute-pair-index[db:parent-localname = 'parentname']");
        db.assertElementExists("//db:geospatial-path-indexes/db:geospatial-path-index[db:path-expression = '/parent/child']");
    }
}
