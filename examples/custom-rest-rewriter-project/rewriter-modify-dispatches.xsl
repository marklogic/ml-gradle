<!--
This transform is used to modify every dispatch element in an XML REST rewriter so that the dispatch
path is to a custom module that can perform some pre-processing before the real MarkLogic dispatch
module is invoked.
-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:r="http://marklogic.com/xdmp/rewriter">

	<xsl:param name="restModulePrefix" />

	<!-- Java XSLT library doesn't seem to like ends-with, so contains is used instead -->
	<xsl:template match="r:dispatch[contains(., '.xqy') or contains(., '.sjs')]">
		<r:dispatch>
			<xsl:apply-templates select="@*"/>
			<xsl:value-of select="concat($restModulePrefix, .)"/>
		</r:dispatch>
	</xsl:template>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
