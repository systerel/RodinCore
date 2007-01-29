<!--
 Copyright (c) 2006 ETH Zurich.
	
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
	
 Xslt stylesheet for pretty printing B machines and contexts.
 This creates an XHTML document which contains no formatting.
 The layout is defined in the css file style.css.
-->
<xsl:transform version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml">
<xsl:output encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

<xsl:param name="name"/>
	
<xsl:template match="/">
	<html>
	<head>
		<link type="text/css" rel="stylesheet" href="style.css"/>
		<title><xsl:value-of select="$name"/></title>
	</head>
	<body>
	<div class="main">
		<!-- COMMON CODE -->
		<xsl:if test="/org.eventb.core.machineFile">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">MACHINE</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="/org.eventb.core.contextFile">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">CONTEXT</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<table class="body" cellspacing="0" cellpadding="0">
			<tr class="line">
				<td align="left" valign="top">
					<xsl:value-of select="$name"/>
				</td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</table>
		
		<!-- MACHINE -->
		<xsl:if test="//org.eventb.core.refinesMachine">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">REFINES</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="mrefines"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.seesContext">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">SEES</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="sees"/>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.variable">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">VARIABLES</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="variables_global"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.invariant">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">INVARIANTS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="invariants"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.variant">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">VARIANT</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="variants"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.event">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">EVENTS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="events"/>
		</xsl:if>
		
		<!-- CONTEXT -->
		<xsl:if test="//org.eventb.core.extendsContext">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">EXTENDS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="cextends"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.carrierSet">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">SETS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="sets"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.constant">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">CONSTANTS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="constants"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.axiom">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">AXIOMS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="axioms"/>
		</xsl:if>
		<xsl:if test="//org.eventb.core.theorem">
			<xsl:call-template name="title1">
				<xsl:with-param name="param1">THEOREMS</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="theorems"/>
		</xsl:if>
		
		<!-- COMMON CODE -->
		<xsl:call-template name="title1">
			<xsl:with-param name="param1">END</xsl:with-param>
		</xsl:call-template>
	</div>
	</body>
	</html>
</xsl:template>
	
<!-- MACHINE -->
<xsl:template name="mrefines">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.refinesMachine">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="sees">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.seesContext">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="variables_global">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="/*/org.eventb.core.variable">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="invariants">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.invariant">
			<tr class="line">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="variants">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.variant">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.expression"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="events">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.event">
			<tr class="line">
				<td align="left" valign="top"><xsl:call-template name="event"/></td>
				<!-- no comment here for events -->
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="event">
	<div class="eventname">
		<xsl:value-of select="@org.eventb.core.label"/>
	</div>
	<xsl:call-template name="comment"/>
	<div class="eventmain">
		<xsl:if test="org.eventb.core.refinesEvent">
			<xsl:call-template name="refines_event"/>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="org.eventb.core.variable">
				<xsl:call-template name="any_event"/>
			</xsl:when>
			<xsl:when test="org.eventb.core.guard">
				<xsl:call-template name="select_event"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="begin_event"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="org.eventb.core.action">
				<xsl:call-template name="actions"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="skip"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="title2"><xsl:with-param name="param1">END</xsl:with-param></xsl:call-template>
	</div>
</xsl:template>

<xsl:template name="any_event">
	<xsl:call-template name="title2"><xsl:with-param name="param1">ANY</xsl:with-param></xsl:call-template>
	<xsl:call-template name="variables_event"/>
	<xsl:call-template name="title2"><xsl:with-param name="param1">WHERE</xsl:with-param></xsl:call-template>
	<xsl:call-template name="guards"/>
	<xsl:call-template name="witness_event"/>
	<xsl:call-template name="title2"><xsl:with-param name="param1">THEN</xsl:with-param></xsl:call-template>
</xsl:template>
	
<xsl:template name="select_event">
	<xsl:call-template name="title2"><xsl:with-param name="param1">WHEN</xsl:with-param></xsl:call-template>
	<xsl:call-template name="guards"/>
	<xsl:call-template name="witness_event"/>
	<xsl:call-template name="title2"><xsl:with-param name="param1">THEN</xsl:with-param></xsl:call-template>
</xsl:template>
	
<xsl:template name="begin_event">
	<xsl:call-template name="witness_event"/>
	<xsl:call-template name="title2"><xsl:with-param name="param1">BEGIN</xsl:with-param></xsl:call-template>
</xsl:template>
	
<xsl:template name="refines_event">
	<xsl:call-template name="title2"><xsl:with-param name="param1">REFINES</xsl:with-param></xsl:call-template>
	<xsl:call-template name="erefines"/>
</xsl:template>
	
<xsl:template name="witness_event">
	<xsl:if test="org.eventb.core.witness">
		<xsl:call-template name="title2"><xsl:with-param name="param1">WITNESSES</xsl:with-param></xsl:call-template>
		<xsl:call-template name="witnesses"/>
	</xsl:if>
</xsl:template>
	
<xsl:template name="witnesses">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<xsl:for-each select="org.eventb.core.witness">
			<tr class="eventline">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="erefines">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<xsl:for-each select="org.eventb.core.refinesEvent">
			<tr class="eventline">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="variables_event">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<xsl:for-each select="org.eventb.core.variable">
			<tr class="eventline">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>	

<xsl:template name="guards">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<xsl:for-each select="org.eventb.core.guard">
			<tr class="eventline">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="skip">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<tr class="eventline">
			<td class="label" align="left" valign="top">skip</td>
		</tr>
	</table>
</xsl:template>
	
<xsl:template name="actions">
	<table class="eventbody" cellspacing="0" cellpadding="0">
		<xsl:for-each select="org.eventb.core.action">
			<tr class="eventline">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.assignment"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<!-- CONTEXTS -->
<xsl:template name="cextends">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.extendsContext">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="constants">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.constant">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="sets">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.carrierSet">
			<tr class="line">
				<td align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="axioms">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.axiom">
			<tr class="line">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<xsl:template name="theorems">
	<table class="body" cellspacing="0" cellpadding="0">
		<xsl:for-each select="//org.eventb.core.theorem">
			<tr class="line">
				<td class="label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="tab" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
				<xsl:call-template name="tdcomment"/>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<!-- COMMON CODE -->
<xsl:template name="tdcomment">
	<xsl:if test="@org.eventb.core.comment">
	<td><xsl:call-template name="comment"/></td>
	</xsl:if>
</xsl:template>
	
<xsl:template name="comment">
	<xsl:choose>
		<xsl:when test="@org.eventb.core.comment and not(contains(@org.eventb.core.comment,'&#10;'))">
			<xsl:call-template name="single_comment"/>
		</xsl:when>
		<xsl:when test="@org.eventb.core.comment and contains(@org.eventb.core.comment,'&#10;')">
			<xsl:call-template name="multi_comment"/>
		</xsl:when>
	</xsl:choose>
</xsl:template>
	
<xsl:template name="single_comment">
	<table class="comment" cellspacing="0" cellpadding="0">
		<tr><td class="begincomment">//</td><td><xsl:value-of select="@org.eventb.core.comment"/></td></tr>
	</table>
</xsl:template>
	
<xsl:template name="multi_comment">
	<table class="comment" cellspacing="0" cellpadding="0">
		<xsl:call-template name="firstline"><xsl:with-param name="comment" select="@org.eventb.core.comment"/></xsl:call-template>
		<xsl:call-template name="middlelines"><xsl:with-param name="comment" select="@org.eventb.core.comment"/></xsl:call-template>
		<xsl:call-template name="lastline"><xsl:with-param name="comment" select="@org.eventb.core.comment"/></xsl:call-template>
	</table>
</xsl:template>
	
<xsl:template name="firstline">
	<xsl:param name="comment"/>
	<tr><td class="begincomment">/*</td><td><xsl:value-of select="substring-before($comment,'&#10;')"/></td><td/></tr>
</xsl:template>
		
<xsl:template name="middlelines">
	<xsl:param name="comment"/>
	<xsl:call-template name="middleline">
		<xsl:with-param name="comment" select="substring-after($comment,'&#10;')"/>
	</xsl:call-template>
</xsl:template>
				
<xsl:template name="middleline">
	<xsl:param name="comment"/>
	<xsl:if test="contains($comment,'&#10;')">
		<tr><td/><td><xsl:value-of select="substring-before($comment,'&#10;')"/></td><td/></tr>
		<xsl:call-template name="middleline">
			<xsl:with-param name="comment" select="substring-after($comment,'&#10;')"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>
		
<xsl:template name="lastline">
	<xsl:param name="comment"/>
	<xsl:choose>
		<xsl:when test="contains($comment,'&#10;')">
			<xsl:call-template name="lastline">
				<xsl:with-param name="comment" select="substring-after($comment,'&#10;')"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<tr><td/><td><xsl:value-of select="$comment"/></td><td class="endcomment">*/</td></tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
	
<xsl:template name="title1">
	<xsl:param name="param1"/>
	<div class="keyword"><xsl:value-of select="$param1"/></div>
</xsl:template>
	
<xsl:template name="title2">
	<xsl:param name="param1"/>
	<div class="eventkeyword"><xsl:value-of select="$param1"/></div>
</xsl:template>
	
</xsl:transform>
