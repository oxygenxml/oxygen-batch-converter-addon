<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xra="http://www.oxygenxml.com/ns/xmlRefactoring/additional_attributes"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    xmlns:xrf="http://www.oxygenxml.com/ns/xmlRefactoring/functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" version="3.0" exclude-result-prefixes="xra xrf xs f">
    
    <!-- DITA Composite output format -->
    <xsl:output name="dita" exclude-result-prefixes="#all" indent="yes" doctype-public="-//OASIS//DTD DITA Composite//EN" doctype-system="ditabase.dtd"/>
   
    <xsl:param name="matchElement" select="('dita', 'section', 'topic', 'task', 'glossentry', 'concept', 'glossgroup', 'reference', 'troubleshooting')"/>
    <xsl:import href="http://www.oxygenxml.com/extensions/frameworks/dita/refactoring/convert-nested-topics-to-new-topic.xsl"/>
    
    <xsl:template match="/*">
        <xsl:choose>
            <xsl:when test="count(.//*[f:match4extraction(.)]) > 0">
                <xsl:element name="map">
                    <title><xsl:value-of select="title"/></title>
                    <xsl:choose>
                        <xsl:when test="'dita' = local-name(.) and count(./*[not(f:match4extraction(.))]) > 0">
                            <xsl:apply-templates  mode="topicRefEmit" select="."/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates  mode="topicRefEmit" select="./*"/>
                        </xsl:otherwise>
                    </xsl:choose>
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
    
    <!--
        We copied this because we want to indent the content of the topics.
    --> 
    <xsl:template name="write-topic">
        <xsl:param name="newDocumentName" as="xs:string"/>
        
        <xsl:variable name="contentToWrite">
            <xsl:choose>
                <xsl:when test="local-name(.) = 'section'">
                    <topic>
                        <xsl:call-template name="copyAttributes"/>
                        <xsl:copy-of select="$newline"/><xsl:apply-templates select="title" mode="write-sect"/><xsl:copy-of select="$newline"/>
                        <body>
                            <xsl:apply-templates select="node() except title" mode="write-sect"/>
                        </body><xsl:copy-of select="$newline"/>
                    </topic>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy>
                        <xsl:call-template name="copyAttributes"/>
                        <xsl:apply-templates select="node() except (*[f:match4extraction(.)])" mode="write-sect"/>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <!-- Get the DOCTYPE or Schema -->
        <xsl:variable name="header" as="xs:string" select="xrf:get-content-before-root()"/>
        <xsl:variable name="hasRNGSchemaReferences" select="f:hasRNGSchemaAssociated($header)"/>
        <xsl:choose>
            <xsl:when test="xs:boolean($hasRNGSchemaReferences)">
                <xsl:message>$newDocumentName: <xsl:value-of select="$newDocumentName"/></xsl:message>
                <xsl:result-document href="{$newDocumentName}" indent="yes">
                    <xsl:copy-of select="$newline"/>
                    <xsl:processing-instruction name="xml-model">
                        <xsl:value-of select="concat('href=', '&quot;', $DEFAULT_RNG_FORMATS($contentToWrite/*/local-name()), '&quot;', ' ', 'schematypens=&quot;http://relaxng.org/ns/structure/1.0&quot;')"/>
                    </xsl:processing-instruction>
                    <xsl:copy-of select="$newline"/>
                    <xsl:copy-of select="$contentToWrite"/>
                </xsl:result-document>
                
            </xsl:when>
            <xsl:otherwise>
                <!-- If no schema or doctype is found, fallback to doctype-->
                <xsl:variable name="outputFormat">
                    <xsl:choose>
                        <xsl:when test="local-name(.) = 'section'">
                            <xsl:value-of select="'topic'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="local-name(.)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:result-document href="{$newDocumentName}" format="{$outputFormat}" indent="yes">
                    <xsl:copy-of select="$contentToWrite"/>
                </xsl:result-document>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
