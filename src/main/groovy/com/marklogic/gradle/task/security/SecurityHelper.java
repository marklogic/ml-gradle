package com.marklogic.gradle.task.security;

import com.marklogic.clientutil.LoggingObject;
import com.marklogic.gradle.xcc.XccHelper;

/**
 * Holding ground for common operations pertaining to security resources.
 */
public class SecurityHelper extends LoggingObject {

    private XccHelper xccHelper;

    public SecurityHelper(XccHelper xccHelper) {
        this.xccHelper = xccHelper;
    }

    public void removeRoles(String... roles) {
        for (String role : roles) {
            String xquery = "declare variable $role external; if (sec:role-exists($role)) then sec:remove-role($role) else ()";
            evaluateAgainstSecurityDatabase(xquery, "role", role);
        }
    }

    public void createRole(String name, String description, String[] roleNames, String[] permissionRoles,
            String[] permissionCapabilities, String[] collections) {
        String xml = "<role>";
        xml += "<name>" + name + "</name>";
        if (description != null) {
            xml += "<description>" + description + "</description>";
        }
        xml += buildXmlForRolesCollectionsAndPermissions(roleNames, permissionRoles, permissionCapabilities,
                collections);
        xml += "</role>";

        String xquery = "declare variable $role external; if (sec:role-exists($role/name)) then () else ";
        xquery += "let $perms := for $perm in $role/permissions/permission return xdmp:permission($perm/role, $perm/capability) ";
        xquery += "return sec:create-role($role/name, $role/description, $role/roles/role/text(), $perms, $role/collections/uri/text())";
        evaluateAgainstSecurityDatabase(xquery, "role", xml);
    }

    public void setPrivilegeForRole(String roleName, String privilegeAction, String privilegeKind) {
        String xquery = "declare variable $action external;  declare variable $kind external; declare variable $role-names external; "
                + "sec:privilege-set-roles($action, $kind, (sec:privilege-get-roles($action, $kind), $role-names))";
        evaluateAgainstSecurityDatabase(xquery, "role-names", roleName, "action", privilegeAction, "kind",
                privilegeKind);
    }

    public void removeUsers(String... usernames) {
        for (String username : usernames) {
            String xquery = "declare variable $username external; if (sec:user-exists($username)) then sec:remove-user($username) else ()";
            evaluateAgainstSecurityDatabase(xquery, "username", username);
        }
    }

    public void createUser(String username, String description, String password, String[] roleNames,
            String[] permissionRoles, String[] permissionCapabilities, String[] collections) {
        String xml = "<user>";
        xml += "<username>" + username + "</username>";
        xml += "<description>" + description + "</description>";
        xml += "<password>" + password + "</password>";
        xml += buildXmlForRolesCollectionsAndPermissions(roleNames, permissionRoles, permissionCapabilities,
                collections);
        xml += "</user>";

        String xquery = "declare variable $user external; if (sec:user-exists($user/username)) then () else ";
        xquery += "let $perms := for $perm in $user/permissions/permission return xdmp:permission($perm/role, $perm/capability) ";
        xquery += "return sec:create-user($user/username, $user/description, $user/password, $user/roles/role/text(), $perms, $user/collections/uri/text())";
        evaluateAgainstSecurityDatabase(xquery, "user", xml);
    }

    private String buildXmlForRolesCollectionsAndPermissions(String[] roleNames, String[] permissionRoles,
            String[] permissionCapabilities, String[] collections) {
        String xml = "";
        if (roleNames != null) {
            xml += "<roles>";
            for (String role : roleNames) {
                xml += "<role>" + role + "</role>";
            }
            xml += "</roles>";
        }
        if (collections != null) {
            xml += "<collections>";
            for (String c : collections) {
                xml += "<uri>" + c + "</uri>";
            }
            xml += "</collections>";
        }
        if (permissionRoles != null) {
            xml += "<permissions>";
            for (int i = 0; i < permissionRoles.length; i++) {
                xml += "<permission>";
                xml += "<role>" + permissionRoles[i] + "</role>";
                xml += "<capability>" + permissionCapabilities[i] + "</capability>";
                xml += "</permission>";
            }
            xml += "</permissions>";
        }
        return xml;
    }

    public String evaluateAgainstSecurityDatabase(String xquery, String... vars) {
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
                + "import module namespace sec = 'http://marklogic.com/xdmp/security' at '/MarkLogic/security.xqy'; "
                + xquery + "\", " + v
                + ", <options xmlns='xdmp:eval'><database>{xdmp:security-database()}</database></options>)";
        return xccHelper.executeXquery(s);
    }

    public XccHelper getXccHelper() {
        return xccHelper;
    }

}
