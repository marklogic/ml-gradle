package com.marklogic.rest.mgmt;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.marklogic.junit.XmlHelper;
import com.marklogic.junit.spring.LoggingTestExecutionListener;
import com.marklogic.xccutil.template.XccTemplate;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@TestExecutionListeners({ LoggingTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class AbstractMgmtTest extends XmlHelper {

    @Autowired
    private ManageConfig manageConfig;

    // Intended to be used by subclasses
    protected ManageClient manageClient;

    @Before
    public void initializeManageClient() {
        manageClient = new ManageClient(manageConfig);
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }

    /**
     * TODO Would be nice to move this and PermissionsFragment up to ml-junit.
     */
    protected PermissionsFragment getDocumentPermissions(String uri, XccTemplate t) {
        String xquery = format("for $perm in xdmp:document-get-permissions('%s') ", uri);
        xquery += "return element {fn:node-name($perm)} {";
        xquery += "  $perm/*,";
        xquery += "  xdmp:eval('import module namespace sec=\"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\"; sec:get-role-names(' || $perm/sec:role-id/fn:string() || ')', (), ";
        xquery += "    <options xmlns='xdmp:eval'><database>{xdmp:security-database()}</database></options>) }";
        return new PermissionsFragment(parse("<permissions>" + t.executeAdhocQuery(xquery) + "</permissions>"));
    }
}
