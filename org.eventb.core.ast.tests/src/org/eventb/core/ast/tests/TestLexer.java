/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.internal.core.parser.ParseResult;
import org.eventb.internal.core.parser.Parser;
import org.eventb.internal.core.parser.Scanner;
import org.eventb.internal.core.parser.Token;

/**
 * This tests the lexical analyzer.
 * <p>
 * Takes as input a string and verifies that the token is correctly recognized.
 * 
 * @author Laurent Voisin
 * @author François Terrier
 * 
 */
public class TestLexer extends TestCase {

        private static final String[][] toBeLexed = {
                { ""                   			 },	// _EOF
                { "(" 				   			 },	// _LPAR
                { ")" 				   			 },	// _RPAR
                { "[" 				   			 },	// _LBRACKET
                { "]" 				   			 },	// _RBRACKET
                { "{" 				   			 },	// _LBRACE
                { "}" 				   			 },	// _RBRACE
                { "\u005e" 			   			 },	// _EXPN
                { "\u00ac" 			   			 },	// _NOT
                { "\u00d7" 			   			 },	// _CPROD
                { "\u03bb" 			   			 },	// _LAMBDA
                { "\u2025"						 },	// _UPTO
                { "\u2115"  		   			 },	// _NATURAL
                { "\u21151" 		   			 },	// _NATURAL1
                { "\u2119"  		   			 },	// _POW
                { "\u21191" 		   			 },	// _POW1
                { "\u2124" 			   			 },	// _INTEGER
                { "\u2192" 			   			 },	// _TFUN
                { "\u2194" 			   			 },	// _REL
                { "\u21a0" 			   			 },	// _TSUR
                { "\u21a3" 			   			 },	// _TINJ
                { "\u21a6" 			   			 },	// _MAPSTO
                { "\u21d2" 			   			 },	// _LIMP
                { "\u21d4" 			   			 },	// _LEQV
                { "\u21f8" 			   			 },	// _PFUN
                { "\u2200" 			   			 },	// _FORALL
                { "\u2203" 			   			 },	// _EXISTS
                { "\u2205" 			   			 },	// _EMPTYSET
                { "\u2208" 			   			 },	// _IN
                { "\u2209" 			   			 },	// _NOTIN
                { "\u2216"			   			 },	// _SETMINUS
                { "\u2217"			   			 },	// _MUL
                { "\u2218" 			   			 },	// _BCOMP
                { "\u2225" 			   			 },	// _PPROD
                { "\u2227" 			   			 },	// _LAND
                { "\u2228" 			   			 },	// _LOR
                { "\u2229" 			   			 },	// _BINTER
                { "\u222a" 			   			 },	// _BUNION
                { "\u2254"			  			 }, // _BEQ
                { ":\u2208"			             }, //_BMO
                { ":\u2223"			             }, // _BST
                { "="      			   			 },	// _EQUAL
                { "\u2260" 			   			 },	// _NOTEQUAL
                { "<"      			   			 },	// _LT
                { "\u2264"	 		  			 },	// _LE
                { ">"                  			 },	// _GT
                { "\u2265" 	   					 },	// _GE
                { "\u2282" 			   			 },	// _SUBSET
                { "\u2284" 			   			 },	// _NOTSUBSET
                { "\u2286" 			   			 },	// _SUBSETEQ
                { "\u2288" 			   			 },	// _NOTSUBSETEQ
                { "\u2297" 			   			 },	// _DPROD
                { "\u22a4" 			   			 },	// _BTRUE
                { "\u22a5" 			   			 },	// _BFALSE
                { "\u22c2" 			   			 },	// _QINTER
                { "\u22c3" 			   			 },	// _QUNION
                { "\u00b7" 			   			 },	// _QDOT
                { "\u25b7" 			   			 },	// _RANRES
                { "\u25c1" 			   			 },	// _DOMRES
                { "\u2900" 			   			 },	// _PSUR
                { "\u2914" 			   			 },	// _PINJ
                { "\u2916" 			   			 },	// _TBIJ
                { "\u2a64" 			   			 },	// _DOMSUB
                { "\u2a65" 			   			 },	// _RANSUB
                { "\ue100" 			   			 },	// _TREL
                { "\ue101" 			   			 },	// _SREL
                { "\ue102" 			   			 },	// _STREL
                { "\ue103" 			   			 },	// _OVR
                { ";"      			   			 },	// _FCOMP
                { "," 	   			   			 },	// _COMMA
                { "+" 	   			   			 },	// _PLUS
                { "\u2212"   			   		 },	// _MINUS
                { "\u00f7" 	   			   		 },	// _DIV
                { "\u2223"			   			 },	// _MID
                { "\u223c"			   			 },	// _CONVERSE
                { "BOOL"   			   			 },	// _BOOL
                { "TRUE"   			   			 },	// _TRUE
                { "FALSE"  			   			 },	// _FALSE
                { "pred"   			   			 },	// _KPRED
                { "succ"   			   			 },	// _KSUCC
                { "mod"				   			 },	// _MOD
                { "bool"			   			 },	// _KBOOL
                { "card"			   			 },	// _KCARD
                { "union"			   			 },	// _KUNION
                { "inter"			   			 },	// _KINTER
                { "dom"				   			 },	// _KDOM
                { "ran"				   			 },	// _KRAN
                { "id"				   			 },	// _KID
                { "finite"			   			 },	// _KFINITE
                { "prj1"			   			 },	// _KPRJ1
                { "prj2"			   			 },	// _KPRJ2
                { "min"				   			 },	// _KMIN
                { "max"				   			 },	// _KMAX
                { ".", "\u2024"		   			 },	// _DOT
                { "x", "_toto", "x'"  			 },	// _IDENT
                { "2", "001"		   			 },	// _INTLIT
                { "\u2982"                       }, // _TYPING
        };
        
        private static final String[] invalidStrings = new String[] {
        	"-",
        };
        
        /**
         * Tests all the tokens that are needed to construct an event-B formula.
         */
        public void testToken() {
        	
        	// Check that all tokens are listed.
        	assertEquals("Length of string table", Parser.getMaxT(), toBeLexed.length);
        	
        	// Check each token string through the lexical analyser.
        	for (int kind = 0; kind < toBeLexed.length; ++ kind) {
        		for (String str : toBeLexed[kind]) {
        			ParseResult result = new ParseResult(FormulaFactory.getDefault());
        			Scanner scanner = new Scanner(str, result);
        			Token t = scanner.Scan();
        			assertEquals(str, t.val);
        			assertEquals(kind, t.kind);
        		}
        	}
        }

        /**
         * Ensure that invalid tokens get rejected.
         */
        public void testInvalidStrings() {
        	FormulaFactory ff = FormulaFactory.getDefault();
        	for (String string : invalidStrings) {
    			final IParseResult result = new ParseResult(ff);
    			Scanner scanner = new Scanner(string, (ParseResult) result);
    			Token t = scanner.Scan();
    			assertTrue("Scanner should have succeeded", result.isSuccess());
    			assertTrue(t.kind == 0);	// _EOF
    			assertTrue("Scanner should have a problem", result.hasProblem());
    			List<ASTProblem> problems = result.getProblems();
    			assertEquals("Should get one problem", 1, problems.size());
    			ASTProblem problem = problems.get(0);
    			assertEquals("The problem should be a warning", ProblemSeverities.Warning, problem.getSeverity());
    			assertEquals("The problem should be a lexer error", ProblemKind.LexerError, problem.getMessage());
        	}
        }
}