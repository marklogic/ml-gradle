package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.appservers.ServerManager;

public class Server extends Resource {

    private String serverName;
    private String groupName;
    private String serverType;
    private Boolean enabled;
    private String root;
    private Integer port;
    private Boolean webDAV;
    private Boolean execute;
    private Boolean displayLastLogin;
    private String address;
    private Integer backlog;
    private Integer threads;
    private Integer requestTimeout;
    private Integer keepAliveTimeout;
    private Integer sessionTimeout;
    private Integer maxTimeLimit;
    private Integer defaultTimeLimit;
    private Integer maxInferenceSize;
    private Integer defaultInferenceSize;
    private Integer staticExpires;
    private Integer preCommitTriggerDepth;
    private Integer preCommitTriggerLimit;
    private String collation;
    private String authentication;
    private Boolean internalSecurity;
    private Integer concurrentRequestLimit;
    private Boolean computeContentLength;
    private Boolean logErrors;
    private Boolean debugAllow;
    private Boolean profileAllow;
    private String defaultXqueryVersion;
    private String multiVersionConcurrencyControl;
    private String distributeTimestamps;
    private String outputSgmlCharacterEntities;
    private String outputEncoding;
    private String outputMethod;
    private String outputByteOrderMark;
    private String outputCdataSectionNamespaceUri;
    private String outputCdataSectionLocalname;
    private String outputDoctypePublic;
    private String outputDoctypeSystem;
    private String outputEscapeUriAttributes;
    private String outputIncludeContentType;
    private String outputIndent;
    private String outputIndentUntyped;
    private String outputIndentTabs;
    private String outputMediaType;
    private String outputNormalizationForm;
    private String outputOmitXmlDeclaration;
    private String outputStandalone;
    private String outputUndeclarePrefixes;
    private String outputVersion;
    private String outputIncludeDefaultAttributes;
    private String defaultErrorFormat;
    private String errorHandler;
    // TODO schemas, namespaces, module-locations, request-blackouts
    private String urlRewriter;
    private Boolean rewriteResolvesGlobally;
    private Boolean sslAllowSslv3;
    private Boolean sslAllowTls;
    private String sslHostname;
    private String sslCiphers;
    private Boolean sslRequireClientCertificate;
    // TODO ssl-client-certificate-authorities
    private String contentDatabase;
    private String modulesDatabase;
    private String defaultUser;
    private String privilege;

    public Server() {
        super();
    }

    public Server(API api, String serverName) {
        super(api);
        this.serverName = serverName;
    }

    @Override
    protected ResourceManager getResourceManager() {
        String name = groupName != null ? groupName : "Default";
        return new ServerManager(getClient(), name);
    }

    @Override
    protected String getResourceId() {
        return serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getWebDAV() {
        return webDAV;
    }

    public void setWebDAV(Boolean webDAV) {
        this.webDAV = webDAV;
    }

    public Boolean getExecute() {
        return execute;
    }

    public void setExecute(Boolean execute) {
        this.execute = execute;
    }

    public Boolean getDisplayLastLogin() {
        return displayLastLogin;
    }

    public void setDisplayLastLogin(Boolean displayLastLogin) {
        this.displayLastLogin = displayLastLogin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getBacklog() {
        return backlog;
    }

    public void setBacklog(Integer backlog) {
        this.backlog = backlog;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(Integer keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public void setMaxTimeLimit(Integer maxTimeLimit) {
        this.maxTimeLimit = maxTimeLimit;
    }

    public Integer getDefaultTimeLimit() {
        return defaultTimeLimit;
    }

    public void setDefaultTimeLimit(Integer defaultTimeLimit) {
        this.defaultTimeLimit = defaultTimeLimit;
    }

    public Integer getMaxInferenceSize() {
        return maxInferenceSize;
    }

    public void setMaxInferenceSize(Integer maxInferenceSize) {
        this.maxInferenceSize = maxInferenceSize;
    }

    public Integer getDefaultInferenceSize() {
        return defaultInferenceSize;
    }

    public void setDefaultInferenceSize(Integer defaultInferenceSize) {
        this.defaultInferenceSize = defaultInferenceSize;
    }

    public Integer getStaticExpires() {
        return staticExpires;
    }

    public void setStaticExpires(Integer staticExpires) {
        this.staticExpires = staticExpires;
    }

    public Integer getPreCommitTriggerDepth() {
        return preCommitTriggerDepth;
    }

    public void setPreCommitTriggerDepth(Integer preCommitTriggerDepth) {
        this.preCommitTriggerDepth = preCommitTriggerDepth;
    }

    public Integer getPreCommitTriggerLimit() {
        return preCommitTriggerLimit;
    }

    public void setPreCommitTriggerLimit(Integer preCommitTriggerLimit) {
        this.preCommitTriggerLimit = preCommitTriggerLimit;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public Boolean getInternalSecurity() {
        return internalSecurity;
    }

    public void setInternalSecurity(Boolean internalSecurity) {
        this.internalSecurity = internalSecurity;
    }

    public Integer getConcurrentRequestLimit() {
        return concurrentRequestLimit;
    }

    public void setConcurrentRequestLimit(Integer concurrentRequestLimit) {
        this.concurrentRequestLimit = concurrentRequestLimit;
    }

    public Boolean getComputeContentLength() {
        return computeContentLength;
    }

    public void setComputeContentLength(Boolean computeContentLength) {
        this.computeContentLength = computeContentLength;
    }

    public Boolean getLogErrors() {
        return logErrors;
    }

    public void setLogErrors(Boolean logErrors) {
        this.logErrors = logErrors;
    }

    public Boolean getDebugAllow() {
        return debugAllow;
    }

    public void setDebugAllow(Boolean debugAllow) {
        this.debugAllow = debugAllow;
    }

    public Boolean getProfileAllow() {
        return profileAllow;
    }

    public void setProfileAllow(Boolean profileAllow) {
        this.profileAllow = profileAllow;
    }

    public String getDefaultXqueryVersion() {
        return defaultXqueryVersion;
    }

    public void setDefaultXqueryVersion(String defaultXqueryVersion) {
        this.defaultXqueryVersion = defaultXqueryVersion;
    }

    public String getMultiVersionConcurrencyControl() {
        return multiVersionConcurrencyControl;
    }

    public void setMultiVersionConcurrencyControl(String multiVersionConcurrencyControl) {
        this.multiVersionConcurrencyControl = multiVersionConcurrencyControl;
    }

    public String getDistributeTimestamps() {
        return distributeTimestamps;
    }

    public void setDistributeTimestamps(String distributeTimestamps) {
        this.distributeTimestamps = distributeTimestamps;
    }

    public String getOutputSgmlCharacterEntities() {
        return outputSgmlCharacterEntities;
    }

    public void setOutputSgmlCharacterEntities(String outputSgmlCharacterEntities) {
        this.outputSgmlCharacterEntities = outputSgmlCharacterEntities;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public String getOutputMethod() {
        return outputMethod;
    }

    public void setOutputMethod(String outputMethod) {
        this.outputMethod = outputMethod;
    }

    public String getOutputByteOrderMark() {
        return outputByteOrderMark;
    }

    public void setOutputByteOrderMark(String outputByteOrderMark) {
        this.outputByteOrderMark = outputByteOrderMark;
    }

    public String getOutputCdataSectionNamespaceUri() {
        return outputCdataSectionNamespaceUri;
    }

    public void setOutputCdataSectionNamespaceUri(String outputCdataSectionNamespaceUri) {
        this.outputCdataSectionNamespaceUri = outputCdataSectionNamespaceUri;
    }

    public String getOutputCdataSectionLocalname() {
        return outputCdataSectionLocalname;
    }

    public void setOutputCdataSectionLocalname(String outputCdataSectionLocalname) {
        this.outputCdataSectionLocalname = outputCdataSectionLocalname;
    }

    public String getOutputDoctypePublic() {
        return outputDoctypePublic;
    }

    public void setOutputDoctypePublic(String outputDoctypePublic) {
        this.outputDoctypePublic = outputDoctypePublic;
    }

    public String getOutputDoctypeSystem() {
        return outputDoctypeSystem;
    }

    public void setOutputDoctypeSystem(String outputDoctypeSystem) {
        this.outputDoctypeSystem = outputDoctypeSystem;
    }

    public String getOutputEscapeUriAttributes() {
        return outputEscapeUriAttributes;
    }

    public void setOutputEscapeUriAttributes(String outputEscapeUriAttributes) {
        this.outputEscapeUriAttributes = outputEscapeUriAttributes;
    }

    public String getOutputIncludeContentType() {
        return outputIncludeContentType;
    }

    public void setOutputIncludeContentType(String outputIncludeContentType) {
        this.outputIncludeContentType = outputIncludeContentType;
    }

    public String getOutputIndent() {
        return outputIndent;
    }

    public void setOutputIndent(String outputIndent) {
        this.outputIndent = outputIndent;
    }

    public String getOutputIndentUntyped() {
        return outputIndentUntyped;
    }

    public void setOutputIndentUntyped(String outputIndentUntyped) {
        this.outputIndentUntyped = outputIndentUntyped;
    }

    public String getOutputIndentTabs() {
        return outputIndentTabs;
    }

    public void setOutputIndentTabs(String outputIndentTabs) {
        this.outputIndentTabs = outputIndentTabs;
    }

    public String getOutputMediaType() {
        return outputMediaType;
    }

    public void setOutputMediaType(String outputMediaType) {
        this.outputMediaType = outputMediaType;
    }

    public String getOutputNormalizationForm() {
        return outputNormalizationForm;
    }

    public void setOutputNormalizationForm(String outputNormalizationForm) {
        this.outputNormalizationForm = outputNormalizationForm;
    }

    public String getOutputOmitXmlDeclaration() {
        return outputOmitXmlDeclaration;
    }

    public void setOutputOmitXmlDeclaration(String outputOmitXmlDeclaration) {
        this.outputOmitXmlDeclaration = outputOmitXmlDeclaration;
    }

    public String getOutputStandalone() {
        return outputStandalone;
    }

    public void setOutputStandalone(String outputStandalone) {
        this.outputStandalone = outputStandalone;
    }

    public String getOutputUndeclarePrefixes() {
        return outputUndeclarePrefixes;
    }

    public void setOutputUndeclarePrefixes(String outputUndeclarePrefixes) {
        this.outputUndeclarePrefixes = outputUndeclarePrefixes;
    }

    public String getOutputVersion() {
        return outputVersion;
    }

    public void setOutputVersion(String outputVersion) {
        this.outputVersion = outputVersion;
    }

    public String getOutputIncludeDefaultAttributes() {
        return outputIncludeDefaultAttributes;
    }

    public void setOutputIncludeDefaultAttributes(String outputIncludeDefaultAttributes) {
        this.outputIncludeDefaultAttributes = outputIncludeDefaultAttributes;
    }

    public String getDefaultErrorFormat() {
        return defaultErrorFormat;
    }

    public void setDefaultErrorFormat(String defaultErrorFormat) {
        this.defaultErrorFormat = defaultErrorFormat;
    }

    public String getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(String errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String getUrlRewriter() {
        return urlRewriter;
    }

    public void setUrlRewriter(String urlRewriter) {
        this.urlRewriter = urlRewriter;
    }

    public Boolean getRewriteResolvesGlobally() {
        return rewriteResolvesGlobally;
    }

    public void setRewriteResolvesGlobally(Boolean rewriteResolvesGlobally) {
        this.rewriteResolvesGlobally = rewriteResolvesGlobally;
    }

    public Boolean getSslAllowSslv3() {
        return sslAllowSslv3;
    }

    public void setSslAllowSslv3(Boolean sslAllowSslv3) {
        this.sslAllowSslv3 = sslAllowSslv3;
    }

    public Boolean getSslAllowTls() {
        return sslAllowTls;
    }

    public void setSslAllowTls(Boolean sslAllowTls) {
        this.sslAllowTls = sslAllowTls;
    }

    public String getSslHostname() {
        return sslHostname;
    }

    public void setSslHostname(String sslHostname) {
        this.sslHostname = sslHostname;
    }

    public String getSslCiphers() {
        return sslCiphers;
    }

    public void setSslCiphers(String sslCiphers) {
        this.sslCiphers = sslCiphers;
    }

    public Boolean getSslRequireClientCertificate() {
        return sslRequireClientCertificate;
    }

    public void setSslRequireClientCertificate(Boolean sslRequireClientCertificate) {
        this.sslRequireClientCertificate = sslRequireClientCertificate;
    }

    public String getContentDatabase() {
        return contentDatabase;
    }

    public void setContentDatabase(String contentDatabase) {
        this.contentDatabase = contentDatabase;
    }

    public String getModulesDatabase() {
        return modulesDatabase;
    }

    public void setModulesDatabase(String modulesDatabase) {
        this.modulesDatabase = modulesDatabase;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
}
