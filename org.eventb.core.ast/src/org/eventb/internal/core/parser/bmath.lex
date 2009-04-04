/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *******************************************************************************/ 
package org.eventb.internal.core.parser;

import static org.eventb.core.ast.LanguageVersion.V1;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;

/**
* Event B mathematical formulas lexer
*/
%%

%class Lexer
%public
%type Token
%unicode
%char
%function next_token

%{
	protected ParseResult result;

	private Token symbol(int kind) {
		return new Token(kind, yytext(), yychar);
	}

	public static boolean isValidIdentifierName(String name) {
		final Reader reader = new StringReader(name);
		final Lexer lexer = new Lexer(reader);
		try {
			final Token token = lexer.next_token();
			return (token != null &&
					token.kind == Parser._IDENT &&
					token.val.equals(name));
		} catch (IOException e) {
			// Should never happen, but let's be conservative
			return false;
		}
	}
%}

WhiteSpace = [:space:] | [\u0009-\u000d\u001c-\u001f]

/* For identifiers, we have to take care of the lambda sign
 * which can not be part of them (contrary to Java).
 */
Identifier =  !(![:jletter:] | "\u03bb") (!(![:jletterdigit:] | "\u03bb"))*
IntegerLiteral = [:digit:]([:digit:])*
Prime = "'"
FullStop = "." | "\u2024"

%%
/* Math Tokens */
"("                   { return symbol(Parser._LPAR); }
")"                   { return symbol(Parser._RPAR); }
"["                   { return symbol(Parser._LBRACKET); }
"]"                   { return symbol(Parser._RBRACKET); }
"{"                   { return symbol(Parser._LBRACE); }
"}"                   { return symbol(Parser._RBRACE); }
";"                   { return symbol(Parser._FCOMP); }
","                   { return symbol(Parser._COMMA); }
"+"                   { return symbol(Parser._PLUS); }
"\u005e"              { return symbol(Parser._EXPN); }
"\u00ac"              { return symbol(Parser._NOT); }
"\u00d7"              { return symbol(Parser._CPROD); }
"\u00f7"			  { return symbol(Parser._DIV); }
"\u03bb"              { return symbol(Parser._LAMBDA); }
"\u2025"              { return symbol(Parser._UPTO); }
"\u2115"              { return symbol(Parser._NATURAL); }
"\u2115" "1"          { return symbol(Parser._NATURAL1); }
"\u2119"              { return symbol(Parser._POW); }
"\u2119" "1"          { return symbol(Parser._POW1); }
"\u2124"              { return symbol(Parser._INTEGER); }
"\u2192"              { return symbol(Parser._TFUN); }
"\u2194"              { return symbol(Parser._REL); }
"\u21a0"              { return symbol(Parser._TSUR); }
"\u21a3"              { return symbol(Parser._TINJ); }
"\u21a6"              { return symbol(Parser._MAPSTO); }
"\u21d2"              { return symbol(Parser._LIMP); }
"\u21d4"              { return symbol(Parser._LEQV); }
"\u21f8"              { return symbol(Parser._PFUN); }
"\u2200"              { return symbol(Parser._FORALL); }
"\u2203"              { return symbol(Parser._EXISTS); }
"\u2205"              { return symbol(Parser._EMPTYSET); }
"\u2208"              { return symbol(Parser._IN); }
"\u2209"              { return symbol(Parser._NOTIN); }
"\u2212"              { return symbol(Parser._MINUS); }
"\u2216"              { return symbol(Parser._SETMINUS); }
"\u2217"              { return symbol(Parser._MUL); }
"\u2218"              { return symbol(Parser._BCOMP); }
"\u2223"              { return symbol(Parser._MID); }
"\u2225"              { return symbol(Parser._PPROD); }
"\u2227"              { return symbol(Parser._LAND); }
"\u2228"              { return symbol(Parser._LOR); }
"\u2229"              { return symbol(Parser._BINTER); }
"\u222a"              { return symbol(Parser._BUNION); }
"\u223c"              { return symbol(Parser._CONVERSE); }
"\u2254"			  { return symbol(Parser._BECEQ); }
":\u2208"			  { return symbol(Parser._BECMO); }
":\u2223"			  { return symbol(Parser._BECST); }
"="                   { return symbol(Parser._EQUAL); }
"\u2260"              { return symbol(Parser._NOTEQUAL); }
"<"                   { return symbol(Parser._LT); }
"\u2264"              { return symbol(Parser._LE); }
">"                   { return symbol(Parser._GT); }
"\u2265"              { return symbol(Parser._GE); }
"\u2282"              { return symbol(Parser._SUBSET); }
"\u2284"              { return symbol(Parser._NOTSUBSET); }
"\u2286"              { return symbol(Parser._SUBSETEQ); }
"\u2288"              { return symbol(Parser._NOTSUBSETEQ); }
"\u2297"              { return symbol(Parser._DPROD); }
"\u22a4"              { return symbol(Parser._BTRUE); }
"\u22a5"              { return symbol(Parser._BFALSE); }
"\u22c2"              { return symbol(Parser._QINTER); }
"\u22c3"              { return symbol(Parser._QUNION); }
"\u00b7"			  { return symbol(Parser._QDOT); }
"\u25b7"              { return symbol(Parser._RANRES); }
"\u25c1"              { return symbol(Parser._DOMRES); }
"\u2900"              { return symbol(Parser._PSUR); }
"\u2914"              { return symbol(Parser._PINJ); }
"\u2916"              { return symbol(Parser._TBIJ); }
"\u2982"              { return symbol(Parser._TYPING); }
"\u2a64"              { return symbol(Parser._DOMSUB); }
"\u2a65"              { return symbol(Parser._RANSUB); }
"\ue100"              { return symbol(Parser._TREL); }
"\ue101"              { return symbol(Parser._SREL); }
"\ue102"              { return symbol(Parser._STREL); }
"\ue103"              { return symbol(Parser._OVR); }
"BOOL"                { return symbol(Parser._BOOL); }
"FALSE"               { return symbol(Parser._FALSE); }
"TRUE"                { return symbol(Parser._TRUE); }
"bool"                { return symbol(Parser._KBOOL); }
"card"                { return symbol(Parser._KCARD); }
"dom"                 { return symbol(Parser._KDOM); }
"finite"              { return symbol(Parser._KFINITE); }
"id"                  { return symbol(Parser._KID); }
"inter"               { return symbol(Parser._KINTER); }
"max"                 { return symbol(Parser._KMAX); }
"min"                 { return symbol(Parser._KMIN); }
"mod"                 { return symbol(Parser._MOD); }
"pred"                { return symbol(Parser._KPRED); }
"prj1"                { return symbol(Parser._KPRJ1); }
"prj2"                { return symbol(Parser._KPRJ2); }
"ran"                 { return symbol(Parser._KRAN); }
"succ"                { return symbol(Parser._KSUCC); }
"union"               { return symbol(Parser._KUNION); }
"partition"           { if (result.getLanguageVersion() == V1) {
                           return symbol(Parser._IDENT);
                        } else {
                           return symbol(Parser._KPARTITION);
                        }
                      }
{FullStop}            { return symbol(Parser._DOT); }
{Identifier}{Prime}?  { return symbol(Parser._IDENT); }
{IntegerLiteral}      { return symbol(Parser._INTLIT); }
{WhiteSpace}+         { /* ignore */ }
<<EOF>>               { return symbol(Parser._EOF); }
.                     { result.addProblem(
							new ASTProblem(
								new SourceLocation(yychar, yychar), 
								ProblemKind.LexerError, 
								ProblemSeverities.Warning, 
								yytext()
						));
					  }
