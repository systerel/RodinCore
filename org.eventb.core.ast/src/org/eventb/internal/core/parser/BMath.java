/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.IndexedSet.OverrideException;

/**
 * @author Nicolas Beauger
 * TODO needs quite a lot of refactorings
 * TODO make extensible grammars (this one + extensions)
 */
public class BMath extends AbstractGrammar {
	
	public static final BMath B_MATH = new BMath();
	static {
		B_MATH.init();
	}
	
	protected BMath() {
		// singleton
	}
	
	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	private final void initTokens() {
		try {
			_EOF = tokens.reserved();
			_IDENT = tokens.reserved();
			_PREDVAR = tokens.reserved();
			_INTLIT = tokens.reserved();
			_LPAR = tokens.add("(");
			_RPAR = tokens.add(")");
			tokens.add("[");
			tokens.add("]");
			tokens.add("{");
			tokens.add("}");
			tokens.add(";");
			tokens.add(",");
			_PLUS = tokens.add("+");
			tokens.add("\u005e");
			tokens.add("\u00ac");
			tokens.add("\u00d7");
			tokens.add("\u00f7");
			tokens.add("\u03bb");
			tokens.add("\u2025");
			tokens.add("\u2115");
			tokens.add("\u21151");
			tokens.add("\u2119");
			tokens.add("\u21191");
			tokens.add("\u2124");
			tokens.add("\u2192");
			tokens.add("\u2194");
			tokens.add("\u21a0");
			tokens.add("\u21a3");
			tokens.add("\u21a6");
			tokens.add("\u21d2");
			tokens.add("\u21d4");
			tokens.add("\u21f8");
			tokens.add("\u2200");
			tokens.add("\u2203");
			tokens.add("\u2205");
			tokens.add("\u2208");
			tokens.add("\u2209");
			tokens.add("\u2212");
			tokens.add("\u2216");
			_MUL = tokens.add("\u2217");
			tokens.add("\u2218");
			tokens.add("\u2223");
			tokens.add("\u2225");
			_LAND = tokens.add("\u2227");
			_LOR = tokens.add("\u2228");
			_BINTER = tokens.add("\u2229");
			_BUNION = tokens.add("\u222a");
			tokens.add("\u223c");
			tokens.add("\u2254");
			tokens.add(":\u2208");
			tokens.add(":\u2223");
			tokens.add("=");
			tokens.add("\u2260");
			tokens.add("<");
			tokens.add("\u2264");
			tokens.add(">");
			tokens.add("\u2265");
			tokens.add("\u2282");
			tokens.add("\u2284");
			tokens.add("\u2286");
			tokens.add("\u2288");
			tokens.add("\u2297");
			_BTRUE = tokens.add("\u22a4");
			_BFALSE = tokens.add("\u22a5");
			tokens.add("\u22c2");
			tokens.add("\u22c3");
			tokens.add("\u00b7");
			tokens.add("\u25b7");
			tokens.add("\u25c1");
			tokens.add("\u2900");
			tokens.add("\u2914");
			tokens.add("\u2916");
			tokens.add("\u2982");
			tokens.add("\u2a64");
			tokens.add("\u2a65");
			tokens.add("\ue100");
			tokens.add("\ue101");
			tokens.add("\ue102");
			tokens.add("\ue103");
			tokens.add("BOOL");
			tokens.add("FALSE");
			tokens.add("TRUE");
			tokens.add("bool");
			tokens.add("card");
			tokens.add("dom");
			tokens.add("finite");
			tokens.add("id");
			tokens.add("inter");
			tokens.add("max");
			tokens.add("min");
			tokens.add("mod");
			tokens.add("pred");
			tokens.add("prj1");
			tokens.add("prj2");
			tokens.add("ran");
			tokens.add("succ");
			tokens.add("union");
			_KPARTITION = tokens.add("partition");
			tokens.add(".");
			tokens.add("\u2024");
			maxT = tokens.reserved();
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static int _EOF;
	private static int _LPAR;
	private static int _RPAR;
//	static int _LBRACKET;
//	static int _RBRACKET;
//	static int _LBRACE;
//	static int _RBRACE;
//	static int _EXPN;
//	static int _NOT;
//	static int _CPROD;
//	static int _LAMBDA;
//	static int _UPTO;
//	static int _NATURAL;
//	static int _NATURAL1;
//	static int _POW;
//	static int _POW1;
//	static int _INTEGER;
//	static int _TFUN;
//	static int _REL;
//	static int _TSUR;
//	static int _TINJ;
//	static int _MAPSTO;
//	static int _LIMP;
//	static int _LEQV;
//	static int _PFUN;
//	static int _FORALL;
//	static int _EXISTS;
//	static int _EMPTYSET;
//	static int _IN;
//	static int _NOTIN;
//	static int _SETMINUS;
	private static int _MUL;
//	static int _BCOMP;
//	static int _PPROD;
	private static int _LAND;
	private static int _LOR;
	private static int _BINTER;
	private static int _BUNION;
//	static int _BECEQ;
//	static int _BECMO;
//	static int _BECST;
//	static int _EQUAL;
//	static int _NOTEQUAL;
//	static int _LT;
//	static int _LE;
//	static int _GT;
//	static int _GE;
//	static int _SUBSET;
//	static int _NOTSUBSET;
//	static int _SUBSETEQ;
//	static int _NOTSUBSETEQ;
//	static int _DPROD;
	private static int _BTRUE;
	private static int _BFALSE;
//	static int _QINTER;
//	static int _QUNION;
//	static int _QDOT;
//	static int _RANRES;
//	static int _DOMRES;
//	static int _PSUR;
//	static int _PINJ;
//	static int _TBIJ;
//	static int _DOMSUB;
//	static int _RANSUB;
//	static int _TREL;
//	static int _SREL;
//	static int _STREL;
//	static int _OVR;
//	static int _FCOMP;
//	static int _COMMA;
	private static int _PLUS;
//	static int _MINUS;
//	static int _DIV;
//	static int _MID;
//	static int _CONVERSE;
//	static int _BOOL;
//	static int _TRUE;
//	static int _FALSE;
//	static int _KPRED;
//	static int _KSUCC;
//	static int _MOD;
//	static int _KBOOL;
//	static int _KCARD;
//	static int _KUNION;
//	static int _KINTER;
//	static int _KDOM;
//	static int _KRAN;
//	static int _KID;
//	static int _KFINITE;
//	static int _KPRJ1;
//	static int _KPRJ2;
//	static int _KMIN;
//	static int _KMAX;
	static int _KPARTITION;
//	static int _DOT;
	static int _IDENT;
	static int _INTLIT;
//	static int _TYPING;
	static int _PREDVAR;
	static int maxT;


	private static final String ARITHMETIC = "Arithmetic";
	private static final String SET_OPERATORS = "Set Operators";
	private static final String LOGIC = "Logic";

	@Override
	public void init() {
		initTokens();
		
		try {
			operatorTag.put(_PLUS, Formula.PLUS);
			operatorTag.put(_MUL, Formula.MUL);
			operatorTag.put(_BUNION, Formula.BUNION);
			operatorTag.put(_BINTER, Formula.BINTER);
			operatorTag.put(_RPAR, Formula.NO_TAG);
			operatorTag.put(_LAND, Formula.LAND);
			operatorTag.put(_LOR, Formula.LOR);

			groupIds.put(PLUS, ARITHMETIC);
			groupIds.put(MUL, ARITHMETIC);
			final OperatorGroup arithGroup = new OperatorGroup(ARITHMETIC);
			arithGroup.addCompatibility(PLUS, PLUS);
			arithGroup.addCompatibility(MUL, MUL);
			arithGroup.addAssociativity(PLUS, MUL);
			operatorGroups.put(ARITHMETIC, arithGroup);

			groupIds.put(BUNION, SET_OPERATORS);
			groupIds.put(BINTER, SET_OPERATORS);
			final OperatorGroup setOpGroup = new OperatorGroup(SET_OPERATORS);
			setOpGroup.addCompatibility(BUNION, BUNION);
			setOpGroup.addCompatibility(BINTER, BINTER);
			operatorGroups.put(SET_OPERATORS, setOpGroup);
			
			groupIds.put(LAND, LOGIC);
			groupIds.put(LOR, LOGIC);
			final OperatorGroup logicGroup = new OperatorGroup(LOGIC);
			logicGroup.addCompatibility(LAND, LAND);
			logicGroup.addCompatibility(LOR, LOR);

			groupIds.put(Formula.NO_TAG, GROUP0);
			groupAssociativity.add(GROUP0, ARITHMETIC);
			groupAssociativity.add(GROUP0, SET_OPERATORS);
			groupAssociativity.add(GROUP0, LOGIC);
			
		} catch (CycleError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		subParsers.put(_INTLIT, Parsers.INTLIT_SUBPARSER);
		subParsers.put(_IDENT, Parsers.FREE_IDENT_SUBPARSER);
		subParsers.put(_BUNION, new Parsers.AssociativeExpressionInfix(BUNION));
		subParsers.put(_BINTER, new Parsers.AssociativeExpressionInfix(BINTER));
		subParsers.put(_PLUS, new Parsers.AssociativeExpressionInfix(PLUS));
		subParsers.put(_MUL, new Parsers.AssociativeExpressionInfix(MUL));
		subParsers.put(_LPAR, new Parsers.ClosedSugar(_RPAR));
		subParsers.put(_BTRUE, new Parsers.LiteralPredicateParser(BTRUE));
		subParsers.put(_BFALSE, new Parsers.LiteralPredicateParser(BFALSE));
		subParsers.put(_LAND, new Parsers.AssociativePredicateInfix(LAND));
		subParsers.put(_LOR, new Parsers.AssociativePredicateInfix(LOR));
	}

}
