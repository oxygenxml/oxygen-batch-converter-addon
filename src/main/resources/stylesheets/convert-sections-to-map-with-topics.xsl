<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xra="http://www.oxygenxml.com/ns/xmlRefactoring/additional_attributes"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" version="3.0" exclude-result-prefixes="xra xs f">
    
    
    <!-- DITA Map output format -->
    <!--<xsl:output method="xml"  exclude-result-prefixes="#all" indent="yes" doctype-public="-//OASIS//DTD DITA Map//EN" doctype-system="map.dtd"/>-->

    <xsl:param name="matchElement" select="('section', 'topic')"></xsl:param>
    <xsl:import href="http://www.oxygenxml.com/extensions/frameworks/dita/refactoring/convert-nested-topics-to-new-topic.xsl"/>
    
    <xsl:template match="/*">
        <xsl:choose>
            <xsl:when test="count(.//*[f:match4extraction(.)]) > 0">
                <xsl:element name="map">
                    <title><xsl:value-of select="title"/></title>
                    <xsl:apply-templates mode="emit" select=".[f:match4extraction(.)]"/>
                    <xsl:apply-templates mode="emit" select=".//*[f:match4extraction(.)]"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template match="*[f:match4extraction(.)]" mode="emit">
        <xsl:variable name="proposalName" select="f:generateOutputFileName(., base-uri())"/>
        <xsl:variable name="name" select="resolve-uri($proposalName, base-uri())"/>
        <topicref>
            <xsl:attribute name="href"><xsl:value-of select="$proposalName"/></xsl:attribute>
            <xsl:call-template name="write-topic" >
                <xsl:with-param name="newDocumentName" select="$name"/>
            </xsl:call-template>
        </topicref>
    </xsl:template>
    
</xsl:stylesheet>
