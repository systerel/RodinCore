<?xml version="1.0" encoding="UTF-8"?>
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
			<xsl:if test="/org.eventb.core.machineFile">
				<div class="level1_keyword">MACHINE</div>
				<table class="level1_table" cellspacing="0" cellpadding="0">
					<xsl:apply-templates select="/org.eventb.core.machineFile"/>
				</table>
			</xsl:if>
			
			<xsl:if test="/org.eventb.core.contextFile">
				<div class="level1_keyword">CONTEXT</div>
				<table class="level1_table" cellspacing="0" cellpadding="0">
					<xsl:apply-templates select="/org.eventb.core.contextFile"/>
				</table>
			</xsl:if>
		</div>
		</body>
	</html>
</xsl:template>
		
<xsl:template match="/org.eventb.core.machineFile">
	<tr class="level1_row">
		<td class="file_content" align="left" valign="top"><xsl:value-of select="$name"/>
		<xsl:call-template name="tdcomment"/>
		
		<!-- MACHINE -->
		<xsl:if test="/*/org.eventb.core.refinesMachine">
			<div class="level2_keyword">REFINES</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.refinesMachine"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.seesContext">
			<div class="level2_keyword">SEES</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.seesContext"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.variable">
			<div class="level2_keyword">VARIABLES</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.variable"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.invariant">
			<div class="level2_keyword">INVARIANTS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.invariant"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.variant">
			<div class="level2_keyword">VARIANT</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.variant"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.event">
			<div class="level2_keyword">EVENTS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.event"/>
			</table>
		</xsl:if>
		
		<div class="level2_keyword">END</div>
	</td></tr>
</xsl:template>

		
<xsl:template match="/org.eventb.core.contextFile">
	<tr class="level1_row">
		<td class="file_content" align="left" valign="top"><xsl:value-of select="$name"/>
		<xsl:call-template name="tdcomment"/>
		
		<!-- CONTEXT -->
		<xsl:if test="/*/org.eventb.core.extendsContext">
			<div class="level2_keyword">EXTENDS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.extendsContext"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.carrierSet">
			<div class="level2_keyword">SETS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.carrierSet"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.constant">
			<div class="level2_keyword">CONSTANTS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.constant"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.axiom">
			<div class="level2_keyword">AXIOMS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.axiom"/>
			</table>
		</xsl:if>
		<xsl:if test="/*/org.eventb.core.theorem">
			<div class="level2_keyword">THEOREMS</div>
			<table class="level2_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="/*/org.eventb.core.theorem"/>
			</table>
		</xsl:if>
		
		<div class="level2_keyword">END</div>
	</td></tr>
</xsl:template>
	
<!-- MACHINE -->
<xsl:template match="/*/org.eventb.core.refinesMachine">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template match="/*/org.eventb.core.seesContext">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template match="/*/org.eventb.core.variable">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="/*/org.eventb.core.invariant">
	<tr class="level2_row">
		<td class="single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="single_content" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template match="/*/org.eventb.core.variant">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.expression"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template match="/*/org.eventb.core.event">
	<tr class="level2_row">
		<td class="event_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>
		<xsl:call-template name="comment"/>

		<xsl:if test="org.eventb.core.refinesEvent">
			<div class="level3_keyword">REFINES</div>
			<table class="level3_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="org.eventb.core.refinesEvent"/>
			</table>
		</xsl:if>
		<xsl:if test="org.eventb.core.variable">
			<div class="level3_keyword">ANY</div>
			<table class="level3_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="org.eventb.core.variable"/>
			</table>
		</xsl:if>
		<xsl:if test="org.eventb.core.guard">
			<xsl:choose>		
				<xsl:when test="org.eventb.core.variable">
					<div class="level3_keyword">WHERE</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="level3_keyword">WHEN</div>
				</xsl:otherwise>
			</xsl:choose>
			<table class="level3_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="org.eventb.core.guard"/>
			</table>
		</xsl:if>
		<xsl:if test="org.eventb.core.witness">
			<div class="level3_keyword">WITNESSES</div>
			<table class="level3_table" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="org.eventb.core.witness"/>
			</table>
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="org.eventb.core.guard">
				<div class="level3_keyword">THEN</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="level3_keyword">BEGIN</div>
			</xsl:otherwise>
		</xsl:choose>
		<table class="level3_table" cellspacing="0" cellpadding="0">
			<xsl:choose>
				<xsl:when test="org.eventb.core.action">
					<xsl:apply-templates select="org.eventb.core.action" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="skip" />
				</xsl:otherwise>
			</xsl:choose>
		</table>

		<div class="level3_keyword">END</div>
	</td></tr>
</xsl:template>
	
<xsl:template match="org.eventb.core.witness">
	<tr class="level3_row">
		<td class="event_single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="event_single_content" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="org.eventb.core.refinesEvent">
	<tr class="level3_row">
		<td class="event_single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="org.eventb.core.variable">
	<tr class="level3_row">
		<td class="event_single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>	

<xsl:template match="org.eventb.core.guard">
	<tr class="level3_row">
		<td class="event_single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="event_single_content" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="org.eventb.core.action">
	<tr class="level3_row">
		<td class="event_single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="event_single_content" valign="top"><xsl:value-of select="@org.eventb.core.assignment"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template name="skip">
	<tr class="level3_row">
		<td class="event_single_content" align="left" valign="top">skip</td>
	</tr>
</xsl:template>
	
<!-- CONTEXTS -->
<xsl:template match="/*/org.eventb.core.extendsContext">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.target"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>

<xsl:template match="/*/org.eventb.core.constant">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="/*/org.eventb.core.carrierSet">
	<tr class="level2_row">
		<td class="single_content" align="left" valign="top"><xsl:value-of select="@org.eventb.core.identifier"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="/*/org.eventb.core.axiom">
	<tr class="level2_row">
		<td class="single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="single_content" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
</xsl:template>
	
<xsl:template match="/*/org.eventb.core.theorem">
	<tr class="level2_row">
		<td class="single_label" align="left" valign="top"><xsl:value-of select="@org.eventb.core.label"/>:</td><td class="single_content" valign="top"><xsl:value-of select="@org.eventb.core.predicate"/></td>
		<xsl:call-template name="tdcomment"/>
	</tr>
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
	
</xsl:transform>
