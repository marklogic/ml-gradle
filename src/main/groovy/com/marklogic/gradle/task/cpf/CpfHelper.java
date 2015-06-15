package com.marklogic.gradle.task.cpf;

import com.marklogic.clientutil.LoggingObject;
import com.marklogic.gradle.xcc.XccHelper;

public class CpfHelper extends LoggingObject {

    private XccHelper xccHelper;
    private String triggersDatabaseName;

    public CpfHelper(XccHelper xccHelper, String triggersDatabaseName) {
        this.xccHelper = xccHelper;
        this.triggersDatabaseName = triggersDatabaseName;
    }

    public void installSystemPipeline(String filename) {
        evaluateAgainstTriggersDatabase("declare variable $filename external; p:insert(xdmp:document-get($filename))",
                "filename", filename);
    }

    public void createDomain(String name, String description, String scope, String scopeUri, String scopeDepth,
            String modulesDatabaseName, String[] pipelineNames, String[] permissions) {

        String pipelinesXml = "<names>";
        for (String n : pipelineNames) {
            pipelinesXml += "<name>" + n + "</name>";
        }
        pipelinesXml += "</names>";

        String xquery = "declare variable $name external; declare variable $description external; ";
        xquery += "declare variable $scope external; declare variable $scope-uri external; declare variable $scope-depth external; ";
        xquery += "declare variable $modules-database-name external; declare variable $pipelines external; declare variable $permissions-xml external; ";
        xquery += "let $domain-id := try {fn:data(dom:get($name)/dom:domain-id) } catch ($e) {()} ";
        xquery += "let $domain-scope := dom:domain-scope($scope, $scope-uri, if ($scope-uri = 'directory') then $scope-depth else ()) ";
        xquery += "let $domain-context := dom:evaluation-context(xdmp:database($modules-database-name), '/') ";
        xquery += "let $pipeline-ids := for $name in $pipelines/element()/fn:string() return xs:unsignedLong(p:get($name)/p:pipeline-id) ";
        xquery += "let $permissions := for $perm in $permissions-xml return xdmp:permission($perm/role, $perm/capability) ";
        xquery += "return if ($domain-id) then (dom:set-description($name, $description), dom:set-domain-scope($name, $domain-scope), ";
        xquery += "dom:set-evaluation-context($name, $domain-context), dom:set-pipelines($name, $pipeline-ids), dom:set-permissions($name, $permissions)) ";
        xquery += "else dom:create($name, $description, $domain-scope, $domain-context, $pipeline-ids, $permissions)";

        evaluateAgainstTriggersDatabase(xquery, "name", name, "description", description, "scope", scope, "scope-uri",
                scopeUri, "scope-depth", scopeDepth, "modules-database-name", modulesDatabaseName, "pipelines",
                pipelinesXml, "permissions-xml", buildPermissionsXml(permissions));
    }

    public void createDomainConfiguration(String restartUser, String modulesDatabaseName, String defaultDomainName,
            String[] permissions) {
        String xquery = "declare variable $restart-user external; declare variable $modules-database-name external; ";
        xquery += "declare variable $default-domain-name external; declare variable $permissions-xml external; ";
        xquery += "let $config := try {dom:configuration-get()} catch ($e) {()} ";
        xquery += "let $context := dom:evaluation-context(xdmp:database($modules-database-name), '/') ";
        xquery += "let $permissions := for $perm in $permissions-xml return xdmp:permission($perm/role, $perm/capability) ";
        xquery += "let $domain-id := fn:data(dom:get($default-domain-name)/dom:domain-id)";
        xquery += "return if ($config) then (dom:configuration-set-restart-user($restart-user), dom:configuration-set-evaluation-context($context), ";
        xquery += "dom:configuration-set-default-domain($domain-id), dom:configuration-set-permissions($permissions)) ";
        xquery += "else dom:configuration-create($restart-user, $context, $domain-id, $permissions)";
        evaluateAgainstTriggersDatabase(xquery, "restart-user", restartUser, "modules-database-name",
                modulesDatabaseName, "default-domain-name", defaultDomainName, "permissions-xml",
                buildPermissionsXml(permissions));
    }

    private String buildPermissionsXml(String[] permissions) {
        String permissionsXml = "<permissions>";
        for (int i = 0; i < permissions.length; i += 2) {
            permissionsXml += "<permission><role>" + permissions[i] + "</role><capability>" + permissions[i + 1]
                    + "</capability></permission>";
        }
        permissionsXml += "</permissions>";
        return permissionsXml;
    }

    public void removeDomain(String domainName) {
        evaluateAgainstTriggersDatabase("declare variable $domain-name external; dom:remove($domain-name)",
                "domain-name", domainName);
    }

    public void addPipelineToDomain(String pipelineId, String domainName) {
        String xquery = "declare variable $domain-name external; declare variable $pipeline-id external; dom:add-pipeline($domain-name, xs:unsignedLong($pipeline-id))";
        evaluateAgainstTriggersDatabase(xquery, "domain-name", domainName, "pipeline-id", pipelineId);
    }

    public String evaluateAgainstTriggersDatabase(String xquery, String... vars) {
        String v = "(";
        for (int i = 0; i < vars.length; i++) {
            String var = vars[i];
            if (i > 0) {
                v += ", ";
            }
            if (i % 2 == 1) {
                if (var.startsWith("(") || var.startsWith("<")) {
                    v += vars[i];
                } else {
                    v += "'" + vars[i] + "'";
                }
            } else {
                v += "xs:QName('" + var + "')";
            }
        }
        v += ")";

        String s = "xdmp:eval(\"xquery version '1.0-ml'; "
                + "import module namespace dom = 'http://marklogic.com/cpf/domains' at '/MarkLogic/cpf/domains.xqy'; "
                + "import module namespace p = 'http://marklogic.com/cpf/pipelines' at '/MarkLogic/cpf/pipelines.xqy'; "
                + xquery + "\", " + v + ", <options xmlns='xdmp:eval'><database>{xdmp:database('"
                + triggersDatabaseName + "')}</database></options>)";
        return xccHelper.executeXquery(s);
    }

    public XccHelper getXccHelper() {
        return xccHelper;
    }
}
