<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:db="http://marklogic.com/manage/package/databases">

  <xsl:param name="mergePackageFilePath" />
  <xsl:variable name="mergePackage" select="document($mergePackageFilePath)/db:package-database" />

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="attribute::*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- For simple elements, choose the one from the package being merged in, if it exists. Else, use the one from the package we're transforming. -->
  <xsl:template match="db:package-database-properties/node()[text()]">
    <xsl:variable name="nodeName" select="name(.)" />
    <xsl:variable name="mergeNode" select="$mergePackage/db:config/db:package-database-properties/node()[name(.) = $nodeName]" />
    <xsl:choose>
      <xsl:when test="$mergeNode">
        <xsl:copy-of select="$mergeNode" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="." />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- For complex elements, grab children elements from both the package we're transforming and the merge package. -->
  <xsl:template match="db:package-database-properties/node()[not(text())]">
    <xsl:variable name="nodeName" select="name(.)" />
    <xsl:copy>
      <xsl:apply-templates />
      <xsl:apply-templates select="$mergePackage/db:config/db:package-database-properties/node()[name(.) = $nodeName]/*" />
    </xsl:copy>
  </xsl:template>

  <!-- For some reason I haven't figured out yet, fields needs its own template -->
  <xsl:template match="/db:package-database/db:config/db:package-database-properties/db:fields">
    <xsl:copy>
      <xsl:apply-templates />
      <xsl:apply-templates select="$mergePackage/db:config/db:package-database-properties/db:fields/db:field" />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="db:links">
    <xsl:copy>
      <xsl:copy-of select="attribute::*" />
      <xsl:apply-templates />
      <xsl:if test="not(db:triggers-database)">
        <xsl:copy-of select="$mergePackage/db:config/db:links/db:triggers-database" />
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="db:links/node()[text()]">
    <xsl:variable name="nodeName" select="name(.)" />
    <xsl:variable name="mergeNode" select="$mergePackage/db:config/db:links/node()[name(.) = $nodeName]" />
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