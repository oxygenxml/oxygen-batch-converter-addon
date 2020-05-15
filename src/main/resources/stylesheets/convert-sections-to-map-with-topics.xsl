<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xra="http://www.oxygenxml.com/ns/xmlRefactoring/additional_attributes"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" version="3.0" exclude-result-prefixes="xra xs f">
    
    <xsl:param name="matchElement" select="('section', 'topic')"></xsl:param>
    <xsl:import href="http://www.oxygenxml.com/extensions/frameworks/dita/refactoring/convert-nested-topics-to-new-topic.xsl"/>
    
    <xsl:template match="/*">
        <xsl:choose>
            <xsl:when test="count(.//*[f:match4extraction(.)]) > 0">
                <xsl:element name="map">
                    <title><xsl:value-of select="title"/></title>
                    <xsl:apply-templates  mode="topicRefEmit" select="."/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="*"  mode="topicRefEmit">
        <xsl:choose>
            <xsl:when test="f:match4extraction(.)">
                <xsl:variable name="proposalName" select="f:generateOutputFileName(., base-uri())"/>
                <topicref>
                    <xsl:attribute name="href"><xsl:value-of select="$proposalName"/></xsl:attribute>
                    <xsl:apply-templates mode="emit" select="."/>
                    <xsl:apply-templates mode="topicRefEmit" select="./*" />
                </topicref>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates mode="topicRefEmit" select="./*"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
