/*
 * Created on 07-jul-2005
 *
 */
package org.eventb.core.ast.tests;

import junit.framework.TestCase;

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
                { "..", "\u2024\u2024", "\u2025" },	// _UPTO
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
                { "\\", "\u2216"	   			 },	// _SETMINUS
                { "*", "\u2217"		   			 },	// _MUL
                { "\u2218" 			   			 },	// _BCOMP
                { "\u2225" 			   			 },	// _PPROD
                { "\u2227" 			   			 },	// _LAND
                { "\u2228" 			   			 },	// _LOR
                { "\u2229" 			   			 },	// _BINTER
                { "\u222a" 			   			 },	// _BUNION
                { "="      			   			 },	// _EQUAL
                { "\u2260" 			   			 },	// _NOTEQUAL
                { "<"      			   			 },	// _LT
                { "<=", "\u2264"	   			 },	// _LE
                { ">"                  			 },	// _GT
                { ">=", "\u2265" 	   			 },	// _GE
                { "\u2282" 			   			 },	// _SUBSET
                { "\u2284" 			   			 },	// _NOTSUBSET
                { "\u2286" 			   			 },	// _SUBSETEQ
                { "\u2288" 			   			 },	// _NOTSUBSETEQ
                { "\u2297" 			   			 },	// _DPROD
                { "\u22a4" 			   			 },	// _BTRUE
                { "\u22a5" 			   			 },	// _BFALSE
                { "\u22c2" 			   			 },	// _QINTER
                { "\u22c3" 			   			 },	// _QUNION
                { "\u22c5" 			   			 },	// _QDOT
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
                { "-" 	   			   			 },	// _MINUS
                { "\u00f7" 	   			   		 },	// _DIV
                { "|", "\u2223"		   			 },	// _MID
                { "~"	   			   			 },	// _CONVERSE
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
                { "x", "_toto"		   			 },	// _IDENT
                { "2", "001"		   			 }	// _INTLIT
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
                                Scanner scanner = new Scanner(str);
                                Token t = scanner.Scan();
                                assertEquals(str, t.val);
                                assertEquals(kind, t.kind);
                        }
                }
        }
}