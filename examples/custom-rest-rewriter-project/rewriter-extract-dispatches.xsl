<!--
This transform is used to extract all of the dispatch elements so that they can be easily processed
via a Groovy XmlSlurper.
-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:r="http://marklogic.com/xdmp/rewriter">
	<!-- Java XSLT library doesn't seem to like ends-with, so contains is used instead -->
	<xsl:template match="r:dispatch[contains(., '.xqy') or contains(., '.sjs')]">
		<dispatch><xsl:value-of select="."/></dispatch>
	</xsl:template>
	<xsl:template match="/">
		<xml>
			<xsl:apply-templates/>
		</xml>
	</xsl:template>
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
</xsl:stylesheet>
