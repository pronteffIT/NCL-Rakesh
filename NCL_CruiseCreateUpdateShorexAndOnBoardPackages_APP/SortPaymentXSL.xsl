<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
    <xsl:template match="PaymentSchedule">
         <PaymentSchedule>
            <xsl:for-each select="Payment">
                <xsl:sort select="@DueDate"/>
                <Payment>
                    <xsl:attribute name="PaymentNumber">
                        <xsl:value-of select="position()" />
                    </xsl:attribute>  
                    <xsl:copy-of select="@DueDate"/>                    
                    <xsl:copy-of select="@Amount"/>
                    <xsl:copy-of select="@DueType"/>
                </Payment>
            </xsl:for-each>
       </PaymentSchedule> 
    </xsl:template>
</xsl:stylesheet>