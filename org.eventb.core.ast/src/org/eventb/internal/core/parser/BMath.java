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
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.internal.core.parser.OperatorRegistry.GROUP0;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extension.CycleError;

/**
 * @author Nicolas Beauger
 * TODO needs refactorings
 */
public class BMath extends AbstractGrammar {
	
	public static final BMath B_MATH = new BMath();
	static {
		B_MATH.init();
	}
	
	protected BMath() {
		// singleton
	}
	
	private static final String NO_TAG_ID = "no tag";
	private static final String LOR_ID = "lor";
	private static final String LAND_ID = "land";
	private static final String BINTER_ID = "binter";
	private static final String BUNION_ID = "bunion";
	private static final String MUL_ID = "mul";
	private static final String PLUS_ID = "plus";
	private static final String ARITHMETIC = "Arithmetic";
	private static final String SET_OPERATORS = "Set Operators";
	private static final String LOGIC = "Logic";
	private static final String QUANTIFIED = "Quantified";
	private static final String FORALL_ID = "for all";
	private static final String EXISTS_ID = "exists";

	private static final String EQUAL_ID = "equal";
	private static final String GT_ID = "greater than";
	private static final String LE_ID = "lower or equal";
	private static final String RELATIONAL = "Relational";

	private static final String FUNIMAGE_ID = "Fun Image";
	private static final String BINARY_EXPRESSION = "Binary Expression";
	private static final String KCARD_ID = "Cardinal";
	private static final String UNARY_EXPRESSION = "Unary Expression";
	private static final String IN_ID = "In";
	private static final String EMPTYSET_ID = "Empty Set";
	private static final String ATOMIC = "Atomic";
	
	
	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	private final void initTokens() {
		_PREDVAR = tokens.reserved();
		tokens.getOrAdd("[");
		tokens.getOrAdd("]");
		tokens.getOrAdd("{");
		tokens.getOrAdd("}");
		tokens.getOrAdd(";");
//			tokens.add(",");
//			_PLUS = tokens.add("+");
		tokens.getOrAdd("\u005e");
		tokens.getOrAdd("\u00ac");
		tokens.getOrAdd("\u00d7");
		tokens.getOrAdd("\u00f7");
		tokens.getOrAdd("\u03bb");
		tokens.getOrAdd("\u2025");
		tokens.getOrAdd("\u2115");
		tokens.getOrAdd("\u21151");
		tokens.getOrAdd("\u2119");
		tokens.getOrAdd("\u21191");
		tokens.getOrAdd("\u2124");
		tokens.getOrAdd("\u2192");
		tokens.getOrAdd("\u2194");
		tokens.getOrAdd("\u21a0");
		tokens.getOrAdd("\u21a3");
		tokens.getOrAdd("\u21a6");
		tokens.getOrAdd("\u21d2");
		tokens.getOrAdd("\u21d4");
		tokens.getOrAdd("\u21f8");
//			tokens.add("\u2200");
		tokens.getOrAdd("\u2203");
		tokens.getOrAdd("\u2205");
		tokens.getOrAdd("\u2208");
		tokens.getOrAdd("\u2209");
		tokens.getOrAdd("\u2212");
		tokens.getOrAdd("\u2216");
//			_MUL = tokens.add("\u2217");
		tokens.getOrAdd("\u2218");
		tokens.getOrAdd("\u2223");
		tokens.getOrAdd("\u2225");
//			_LAND = tokens.add("\u2227");
//			_LOR = tokens.add("\u2228");
//			_BINTER = tokens.add("\u2229");
//			_BUNION = tokens.add("\u222a");
		tokens.getOrAdd("\u223c");
		tokens.getOrAdd("\u2254");
		tokens.getOrAdd(":\u2208");
		tokens.getOrAdd(":\u2223");
//			tokens.add("=");
		tokens.getOrAdd("\u2260");
		tokens.getOrAdd("<");
		tokens.getOrAdd("\u2264");
		tokens.getOrAdd(">");
		tokens.getOrAdd("\u2265");
		tokens.getOrAdd("\u2282");
		tokens.getOrAdd("\u2284");
		tokens.getOrAdd("\u2286");
		tokens.getOrAdd("\u2288");
		tokens.getOrAdd("\u2297");
//			_BTRUE = tokens.add("\u22a4");
//			_BFALSE = tokens.add("\u22a5");
		tokens.getOrAdd("\u22c2");
		tokens.getOrAdd("\u22c3");
//			tokens.add("\u00b7");
		tokens.getOrAdd("\u25b7");
		tokens.getOrAdd("\u25c1");
		tokens.getOrAdd("\u2900");
		tokens.getOrAdd("\u2914");
		tokens.getOrAdd("\u2916");
		tokens.getOrAdd("\u2982");
		tokens.getOrAdd("\u2a64");
		tokens.getOrAdd("\u2a65");
		tokens.getOrAdd("\ue100");
		tokens.getOrAdd("\ue101");
		tokens.getOrAdd("\ue102");
		tokens.getOrAdd("\ue103");
		tokens.getOrAdd("BOOL");
		tokens.getOrAdd("FALSE");
		tokens.getOrAdd("TRUE");
		tokens.getOrAdd("bool");
//		tokens.getOrAdd("card");
		tokens.getOrAdd("dom");
		tokens.getOrAdd("finite");
		tokens.getOrAdd("id");
		tokens.getOrAdd("inter");
		tokens.getOrAdd("max");
		tokens.getOrAdd("min");
		tokens.getOrAdd("mod");
		tokens.getOrAdd("pred");
		tokens.getOrAdd("prj1");
		tokens.getOrAdd("prj2");
		tokens.getOrAdd("ran");
		tokens.getOrAdd("succ");
		tokens.getOrAdd("union");
		_KPARTITION = tokens.getOrAdd("partition");
//			tokens.add(".");
		tokens.getOrAdd("\u2024");
	}

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
//	private static int _MUL;
//	static int _BCOMP;
//	static int _PPROD;
//	private static int _LAND;
//	private static int _LOR;
//	private static int _BINTER;
//	private static int _BUNION;
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
//	private static int _BTRUE;
//	private static int _BFALSE;
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
//	private static int _PLUS;
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
//	static int _TYPING;
	static int _PREDVAR;


	@Override
	public void init() {
		super.init();
		initTokens();
		
		opRegistry.addOperator(Formula.NO_TAG, NO_TAG_ID, GROUP0);
		try {
			addOperator("\u222a", BUNION, BUNION_ID, SET_OPERATORS, new Parsers.AssociativeExpressionInfix(BUNION));
			addOperator("\u2229", BINTER, BINTER_ID, SET_OPERATORS, new Parsers.AssociativeExpressionInfix(BINTER));
			addOperator("+", PLUS, PLUS_ID, ARITHMETIC, new Parsers.AssociativeExpressionInfix(PLUS));
			addOperator("\u2217", MUL, MUL_ID, ARITHMETIC, new Parsers.AssociativeExpressionInfix(MUL));
			addLiteralOperator("\u22a4", BTRUE, new Parsers.LiteralPredicateParser(BTRUE));
			addLiteralOperator("\u22a5", BFALSE, new Parsers.LiteralPredicateParser(BFALSE));
			addOperator("\u2227", LAND, LAND_ID, LOGIC, new Parsers.AssociativePredicateInfix(LAND));
			addOperator("\u2228", LOR, LOR_ID, LOGIC, new Parsers.AssociativePredicateInfix(LOR));
			addQuantifiedOperator("\u2200", ",", "\u00b7", FORALL, FORALL_ID, QUANTIFIED);
			addQuantifiedOperator("\u2203", ",", "\u00b7", EXISTS, EXISTS_ID, QUANTIFIED);
			addOperator("=", EQUAL, EQUAL_ID, RELATIONAL, new Parsers.RelationalPredicateInfix(EQUAL));
			addOperator(">", GT, GT_ID, RELATIONAL, new Parsers.RelationalPredicateInfix(GT));
			addOperator("≤", LE, LE_ID, RELATIONAL, new Parsers.RelationalPredicateInfix(LE));
			addOperator("(", FUNIMAGE, FUNIMAGE_ID, BINARY_EXPRESSION, Parsers.FUN_IMAGE);
			addOperator("card", KCARD, KCARD_ID, UNARY_EXPRESSION, new Parsers.UnaryExpression(KCARD));
			addOperator("\u2208", IN, IN_ID, RELATIONAL, new Parsers.RelationalPredicateInfix(IN));
			addOperator("\u2205", EMPTYSET, EMPTYSET_ID, ATOMIC, new Parsers.AtomicExpression(EMPTYSET));
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		opRegistry.addCompatibility(PLUS_ID, PLUS_ID);
		opRegistry.addCompatibility(MUL_ID, MUL_ID);
		opRegistry.addCompatibility(BUNION_ID, BUNION_ID);
		opRegistry.addCompatibility(BINTER_ID, BINTER_ID);

		try {
			opRegistry.addPriority(PLUS_ID, MUL_ID);
			opRegistry.addGroupPriority(QUANTIFIED, RELATIONAL);
			opRegistry.addGroupPriority(QUANTIFIED, SET_OPERATORS);
			opRegistry.addGroupPriority(QUANTIFIED, ARITHMETIC);
			opRegistry.addGroupPriority(QUANTIFIED, LOGIC);
			opRegistry.addGroupPriority(QUANTIFIED, BINARY_EXPRESSION);
		} catch (CycleError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int getPARTITION() {
		return _KPARTITION;
	}
	
	public int getPREDVAR() {
		return _PREDVAR;
	}
	
}
