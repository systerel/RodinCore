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

import static org.eventb.core.ast.Formula.*;
import static org.eventb.internal.core.parser.OperatorRegistry.GROUP0;
import static org.eventb.internal.core.parser.Parsers.*;

import org.eventb.core.ast.extension.CycleError;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.Parsers.BinaryExpressionInfix;

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
	
	private static final String RELOP_PRED = "Relational Operator Predicate";
	private static final String QUANTIFICATION = "Quantification";
	private static final String PAIR = "Pair";
	private static final String RELATION = "Relation";
	private static final String BINOP = "Binary Operator";
	private static final String INTERVAL = "Interval";
	private static final String ARITHMETIC = "Arithmetic";
	private static final String UNARY_RELATION = "Unary Relation";
	private static final String TYPED = "Typed";
	private static final String FUNCTIONAL = "Functional";
	private static final String BRACE_SETS = "Brace Sets";
	private static final String IDENT_LIST = "Identifier List";
	private static final String IDENT_MAPLET_LIST = "Ident Maplet List";
	private static final String INFIX_SUBST = "Infix Substitution";
	private static final String QUANTIFIED_PRED = "Quantified";
	private static final String LOGIC_PRED = "Logic Predicate";
	private static final String INFIX_PRED = "Infix Predicate";
	private static final String NOT_PRED = "Not Predicate";
	private static final String ATOMIC_PRED = "Atomic Predicate";
	private static final String ATOMIC_EXPR = "Atomic Expression";
	private static final String EMPTY_SET = "Empty Set";
	private static final String BOUND_UNARY = "Bound Unary";
	private static final String BOOL = "Bool";

	private static final String LOR_ID = "lor";
	private static final String LAND_ID = "land";
	private static final String BINTER_ID = "binter";
	private static final String BUNION_ID = "bunion";
	private static final String MUL_ID = "mul";
	private static final String PLUS_ID = "plus";
	private static final String FORALL_ID = "for all";
	private static final String EXISTS_ID = "exists";

	private static final String EQUAL_ID = "equal";
	private static final String GT_ID = "greater than";
	private static final String LE_ID = "lower or equal";

	private static final String FUNIMAGE_ID = "Fun Image";
	private static final String KCARD_ID = "Cardinal";
	private static final String IN_ID = "In";
	private static final String EMPTYSET_ID = "Empty Set";
	private static final String BTRUE_ID = "B True";
	private static final String BFALSE_ID = "B False";
	private static final String SETEXT_ID = "Set Extension";
	private static final String INTEGER_ID = "Integer";
	private static final String POW_ID = "Power Set";
	private static final String CPROD_ID = "Cartesian Product";
	private static final String OFTYPE_ID = "Oftype";
	private static final String CSET_ID = "Comprehension Set";
	private static final String MAPSTO_ID = "Maps to";
	private static final String LAMBDA_ID = "Lambda";
	private static final String LIMP_ID = "Logical Implication";
	private static final String TRUE_ID = "True";
	private static final String NOT_ID = "Not";
	private static final String TFUN_ID = "Total Function";
	private static final String UPTO_ID = "Up To";
	private static final String CONVERSE_ID = "Converse";
	private static final String KBOOL_ID = "To Bool";
	private static final String KPARTITION_ID = "Partition";
	private static final String KFINITE_ID = "Finite";
	private static final String PRED_VAR_ID = "Predicate Variable";
	private static final String KID_GEN_ID = "Identity";
	private static final String KPRJ1_GEN_ID = "Projection 1";
	private static final String KPRJ2_GEN_ID = "Projection 2";
	
	
	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	private final void initTokens() {
		_PREDVAR = tokens.reserved();
		tokens.getOrAdd("[");
		tokens.getOrAdd("]");
		_LBRACE = tokens.getOrAdd("{");
		_RBRACE = tokens.getOrAdd("}");
		tokens.getOrAdd(";");
//			tokens.add(",");
//			_PLUS = tokens.add("+");
		tokens.getOrAdd("\u005e");
		tokens.getOrAdd("\u00ac");
		tokens.getOrAdd("\u00d7");
		tokens.getOrAdd("\u00f7");
		_LAMBDA = tokens.getOrAdd("\u03bb");
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
		_MAPSTO = tokens.getOrAdd("\u21a6");
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
		_MID = tokens.getOrAdd("\u2223");
		tokens.getOrAdd("\u2225");
//			_LAND = tokens.add("\u2227");
//			_LOR = tokens.add("\u2228");
//			_BINTER = tokens.add("\u2229");
//			_BUNION = tokens.add("\u222a");
		tokens.getOrAdd("\u223c");
		_BECEQ = tokens.getOrAdd("\u2254");
		_BECMO = tokens.getOrAdd(":\u2208");
		_BECST = tokens.getOrAdd(":\u2223");
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
		_DOT = tokens.getOrAdd("\u00b7");
		tokens.getOrAdd("\u25b7");
		tokens.getOrAdd("\u25c1");
		tokens.getOrAdd("\u2900");
		tokens.getOrAdd("\u2914");
		tokens.getOrAdd("\u2916");
		_TYPING = tokens.getOrAdd("\u2982");
		tokens.getOrAdd("\u2a64");
		tokens.getOrAdd("\u2a65");
		tokens.getOrAdd("\ue100");
		tokens.getOrAdd("\ue101");
		tokens.getOrAdd("\ue102");
		tokens.getOrAdd("\ue103");
		tokens.getOrAdd("BOOL");
		tokens.getOrAdd("FALSE");
//		tokens.getOrAdd("TRUE");
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
	static int _LBRACE;
	static int _RBRACE;
//	static int _EXPN;
//	static int _NOT;
//	static int _CPROD;
	static int _LAMBDA;
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
	static int _MAPSTO;
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
	static int _BECEQ;
	static int _BECMO;
	static int _BECST;
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
	static int _MID;
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
	static int _DOT;
	static int _TYPING;
	static int _PREDVAR;


	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		super.init();
		initTokens();
		
		try {
			addOperator("\u222a", BUNION_ID, BINOP, new AssociativeExpressionInfix(BUNION));
			addOperator("\u2229", BINTER_ID, BINOP, new AssociativeExpressionInfix(BINTER));
			addOperator("+", PLUS_ID, ARITHMETIC, new AssociativeExpressionInfix(PLUS));
			addOperator("\u2217", MUL_ID, ARITHMETIC, new AssociativeExpressionInfix(MUL));
			addOperator("\u22a4", BTRUE_ID, ATOMIC_PRED, new LiteralPredicateParser(BTRUE));
			addOperator("\u22a5", BFALSE_ID, ATOMIC_PRED, new LiteralPredicateParser(BFALSE));
			addOperator("\u2227", LAND_ID, LOGIC_PRED, new AssociativePredicateInfix(LAND));
			addOperator("\u2228", LOR_ID, LOGIC_PRED, new AssociativePredicateInfix(LOR));
			addOperator("\u2200", FORALL_ID, QUANTIFIED_PRED, new QuantifiedPredicateParser(FORALL));
			addOperator("\u2203", EXISTS_ID, QUANTIFIED_PRED, new QuantifiedPredicateParser(EXISTS));
			addOperator("=", EQUAL_ID, RELOP_PRED, new RelationalPredicateInfix(EQUAL));
			addOperator(">", GT_ID, RELOP_PRED, new RelationalPredicateInfix(GT));
			addOperator("≤", LE_ID, RELOP_PRED, new RelationalPredicateInfix(LE));
			addOperator("(", FUNIMAGE_ID, FUNCTIONAL, FUN_IMAGE);
			addOperator("card", KCARD_ID, FUNCTIONAL, new UnaryExpressionParser(KCARD));
			addOperator("\u2208", IN_ID, RELOP_PRED, new RelationalPredicateInfix(IN));
			addOperator("\u2205", EMPTYSET_ID, EMPTY_SET, new AtomicExpressionParser(EMPTYSET));
			// FIXME is there a problem having the same group for V1 and V2 operators ?
			addOperator("id", KID_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KID, KID_GEN));
			addOperator("prj1", KPRJ1_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ1, KPRJ1_GEN));
			addOperator("prj2", KPRJ2_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ2, KPRJ2_GEN));
			addOperator("{", SETEXT_ID, BRACE_SETS, SETEXT_PARSER);
			addOperator("{", CSET_ID, BRACE_SETS, CSET_EXPLICIT);
			addOperator("{", CSET_ID, BRACE_SETS, CSET_IMPLICIT);
			addOperator("\u03bb", LAMBDA_ID, QUANTIFICATION, CSET_LAMBDA);
			addOperator("\u2124", INTEGER_ID, ATOMIC_EXPR, new AtomicExpressionParser(INTEGER));
			addOperator("\u2119", POW_ID, BOUND_UNARY, new UnaryExpressionParser(POW));
			addOperator("\u00d7", CPROD_ID, BINOP, new BinaryExpressionInfix(CPROD));
			addOperator("\u2982", OFTYPE_ID, TYPED, OFTYPE);
			addOperator("\u21a6", MAPSTO_ID, PAIR, new BinaryExpressionInfix(MAPSTO));
			addOperator("\u21d2", LIMP_ID, INFIX_PRED, new BinaryPredicateParser(LIMP));
			addOperator("TRUE", TRUE_ID, ATOMIC_EXPR, new AtomicExpressionParser(TRUE));
			addOperator("\u00ac", NOT_ID, NOT_PRED, NOT_PARSER);
			addOperator("\u2192", TFUN_ID, RELATION, new BinaryExpressionInfix(TFUN));
			addOperator("\u2025", UPTO_ID, INTERVAL, new BinaryExpressionInfix(UPTO));
			addOperator("\u223c", CONVERSE_ID, UNARY_RELATION, CONVERSE_PARSER);
			addOperator("bool", KBOOL_ID, BOOL, KBOOL_PARSER);
			addOperator("partition", KPARTITION_ID, ATOMIC_PRED, PARTITION_PARSER);
			addOperator("finite", KFINITE_ID, ATOMIC_PRED, FINITE_PARSER);
			addOperator(_PREDVAR, PRED_VAR_ID, GROUP0, PRED_VAR_SUBPARSER);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		opRegistry.addCompatibility(BUNION_ID, BUNION_ID);
		opRegistry.addCompatibility(BINTER_ID, BINTER_ID);
		
		opRegistry.addCompatibility(PLUS_ID, PLUS_ID);
		opRegistry.addCompatibility(MUL_ID, MUL_ID);

		opRegistry.addCompatibility(FUNIMAGE_ID, FUNIMAGE_ID);
		
		opRegistry.addCompatibility(FORALL_ID, EXISTS_ID);
		opRegistry.addCompatibility(EXISTS_ID, FORALL_ID);
		
		opRegistry.addCompatibility(LAND_ID, LAND_ID);
		opRegistry.addCompatibility(LOR_ID, LOR_ID);
		
		try {
			opRegistry.addPriority(PLUS_ID, MUL_ID);
			
			opRegistry.addGroupPriority(QUANTIFIED_PRED, INFIX_PRED);
			opRegistry.addGroupPriority(INFIX_PRED, LOGIC_PRED);
			opRegistry.addGroupPriority(LOGIC_PRED, NOT_PRED);
			opRegistry.addGroupPriority(NOT_PRED, ATOMIC_PRED);
			opRegistry.addGroupPriority(ATOMIC_PRED, RELOP_PRED);
			opRegistry.addGroupPriority(GROUP0, QUANTIFICATION);
			opRegistry.addGroupPriority(GROUP0, QUANTIFIED_PRED);
			opRegistry.addGroupPriority(RELOP_PRED, PAIR);
			opRegistry.addGroupPriority(QUANTIFICATION, RELOP_PRED);
			opRegistry.addGroupPriority(GROUP0, QUANTIFICATION);// TODO remove: redundant
			opRegistry.addGroupPriority(GROUP0, TYPED);
			opRegistry.addGroupPriority(TYPED, QUANTIFICATION);
			opRegistry.addGroupPriority(QUANTIFICATION, PAIR);
			opRegistry.addGroupPriority(PAIR, RELATION);
			opRegistry.addGroupPriority(RELATION, BINOP);
			opRegistry.addGroupPriority(BINOP, INTERVAL);
			opRegistry.addGroupPriority(INTERVAL, ARITHMETIC);
			opRegistry.addGroupPriority(ARITHMETIC, UNARY_RELATION);
			opRegistry.addGroupPriority(UNARY_RELATION, BOUND_UNARY);
			opRegistry.addGroupPriority(BOUND_UNARY, BOOL);
			opRegistry.addGroupPriority(GROUP0, BRACE_SETS);
			opRegistry.addGroupPriority(BRACE_SETS, QUANTIFICATION);
			opRegistry.addGroupPriority(BRACE_SETS, QUANTIFIED_PRED);
			opRegistry.addGroupPriority(GROUP0, FUNCTIONAL);
			opRegistry.addGroupPriority(FUNCTIONAL, QUANTIFICATION);
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
