package com.marklogic.gradle.task.alerting;

import com.marklogic.gradle.xcc.XccHelper;
import com.rjrudin.marklogic.client.LoggingObject;

public class AlertHelper extends LoggingObject {

    private XccHelper xccHelper;

    public AlertHelper(XccHelper xccHelper) {
        this.xccHelper = xccHelper;
    }

    public void insertAlertConfig(String uri, String name, String description, String optionsXml,
            String... cpfDomainNames) {
        String names = "<cpfDomainNames>";
        for (String s : cpfDomainNames) {
            names += "<name>" + s + "</name>";
        }
        names += "</cpfDomainNames>";

        String xquery = "declare variable $uri external; declare variable $name external; ";
        xquery += "declare variable $description external; declare variable $options external; declare variable $cpf-domain-names external; ";
        xquery += "let $config := alert:make-config($uri, $name, $description, $options) ";
        xquery += "return if ($cpf-domain-names/name) then alert:config-insert(alert:config-set-cpf-domain-names($config, $cpf-domain-names/name/text())) ";
        xquery += "else alert:config-insert($config)";

        evaluateAgainstContentDatabase(xquery, "uri", uri, "name", name, "description", description, "options",
                optionsXml, "cpf-domain-names", names);
    }

    public void deleteAlertConfig(String uri, boolean deleteRules) {
        String xquery = "declare variable $uri external; let $config := alert:config-get($uri)";
        xquery += "where $config return xdmp:node-delete($config/alert:cpf-domains) ";
        evaluateAgainstContentDatabase(xquery, "uri", uri);

        if (deleteRules) {
            xquery = "declare variable $uri external; let $config := alert:config-get($uri)";
            xquery += "where $config return alert:config-delete($uri)";
            evaluateAgainstContentDatabase(xquery, "uri", uri);
        } else {
            xquery = "declare variable $uri external; let $config := alert:config-get($uri) where $config return ";
            xquery += "for $doc-uri in cts:uris((), (), cts:collection-query($uri)) ";
            xquery += "where fn:not(fn:starts-with($doc-uri, fn:concat($uri, '/rules'))) ";
            xquery += "return xdmp:document-delete($doc-uri)";
            evaluateAgainstContentDatabase(xquery, "uri", uri);
        }
    }

    public void insertAlertAction(String configUri, String name, String description, String module, String optionsXml) {
        String xquery = "declare variable $uri external; declare variable $name external; ";
        xquery += "declare variable $description external; declare variable $module external; declare variable $options external; ";
        xquery += "alert:action-insert($uri, alert:make-action($name, $description, xdmp:modules-database(), '/', $module, $options))";
        evaluateAgainstContentDatabase(xquery, "uri", configUri, "name", name, "description", description, "module",
                module, "options", optionsXml);
    }

    public String evaluateAgainstContentDatabase(String xquery, String... vars) {
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
                + "import module namespace alert = 'http://marklogic.com/xdmp/alert' at '/MarkLogic/alert.xqy'; "
                + xquery + "\", " + v
                + ", <options xmlns='xdmp:eval'><database>{xdmp:database()}</database></options>)";
        return xccHelper.executeXquery(s);
    }
}
