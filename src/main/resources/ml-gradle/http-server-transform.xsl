<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:srv="http://marklogic.com/manage/package/servers">

  <xsl:param name="mergePackageFilePath" />
  <xsl:variable name="mergePackage" select="document($mergePackageFilePath)/srv:package-http-server" />

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="attribute::*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="srv:package-http-properties/node()">
    <xsl:variable name="nodeName" select="name(.)" />
    <xsl:variable name="mergeNode" select="$mergePackage/srv:config/srv:package-http-properties/node()[name(.) = $nodeName]" />
    <xsl:choose>
      <xsl:when test="$mergeNode">
        <xsl:copy-of select="$mergeNode" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="." />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="srv:links/node()">
    <xsl:variable name="nodeName" select="name(.)" />
    <xsl:variable name="mergeNode" select="$mergePackage/srv:config/srv:links/node()[name(.) = $nodeName]" />
    <xsl:choose>
      <xsl:when test="$mergeNode">
        <xsl:copy-of select="$mergeNode" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="." />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>