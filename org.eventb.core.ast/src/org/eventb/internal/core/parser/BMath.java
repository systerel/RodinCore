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
import static org.eventb.internal.core.parser.SubParsers.*;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.internal.core.parser.GenParser.OverrideException;

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
	private static final String RELATION = "Set of Relations";
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
	private static final String BINTER_ID = "Binary Intersection";
	private static final String BUNION_ID = "Binary Union";
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
	private static final String BCOMP_ID = "Backward Composition";
	private static final String FCOMP_ID = "Forward Composition";
	private static final String OVR_ID = "Overload";
	private static final String LEQV_ID = "Equivalent";
	private static final String NATURAL_ID = "Natural";
	private static final String NATURAL1_ID = "Natural1";
	private static final String BOOL_ID = "Bool Type";
	private static final String FALSE_ID = "False";
	private static final String KPRED_ID = "Predecessor";
	private static final String KSUCC_ID = "Successor";
	private static final String RELIMAGE_ID = "Relational Image";
	private static final String REL_ID = "Relation";
	private static final String TREL_ID = "Total Relation";
	private static final String SREL_ID = "Surjective Relation";
	private static final String STREL_ID = "Surjective Total Relation";
	private static final String PFUN_ID = "Partial Function";
	private static final String PINJ_ID = "Partial Injection";
	private static final String TINJ_ID = "Total Injection";
	private static final String PSUR_ID = "Partial Surjection";
	private static final String TSUR_ID = "Total Surjection";
	private static final String TBIJ_ID = "Total Bijection";
	private static final String SETMINUS_ID = "Set Minus";
	private static final String PPROD_ID = "Parallel Product";
	private static final String DPROD_ID = "Direct Product";
	private static final String DOMRES_ID = "Domain Restriction";
	private static final String DOMSUB_ID = "Domain Subtraction";
	private static final String RANRES_ID = "Range Restriction";
	private static final String RANSUB_ID = "Range Subtraction";
	private static final String MINUS_ID = "Minus";
	private static final String DIV_ID = "Integer Division";
	private static final String MOD_ID = "Modulo";
	private static final String EXPN_ID = "Integer Exponentiation";
	private static final String NOTEQUAL_ID = "Not Equal";
	private static final String LT_ID = "Lower Than";
	private static final String GE_ID = "Greater or Equal";
	private static final String NOTIN_ID = "Not In";
	private static final String SUBSET_ID = "Subset";
	private static final String NOTSUBSET_ID = "Not Subset";
	private static final String SUBSETEQ_ID = "Subset or Equal";
	private static final String NOTSUBSETEQ_ID = "Not Subset or Equal";
	private static final String POW1_ID = "Powerset 1";
	private static final String KDOM_ID = "Domain";
	private static final String KRAN_ID = "Range";
	private static final String KMIN_ID = "Min";
	private static final String KMAX_ID = "Max";
	private static final String QUNION_ID = "Quantified Union";
	private static final String QINTER_ID = "Quantified Intersection";
	private static final String KUNION_ID = "Unary Union";
	private static final String KINTER_ID = "Unary Intersection";
	
	
	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	private final void initTokens() {
		_PREDVAR = tokens.reserved();
		_RBRACKET = tokens.getOrAdd("]");
		_RBRACE = tokens.getOrAdd("}");
		_LAMBDA = tokens.getOrAdd("\u03bb");
		_MAPSTO = tokens.getOrAdd("\u21a6");
		_MID = tokens.getOrAdd("\u2223");
		_BECEQ = tokens.getOrAdd("\u2254");
		_BECMO = tokens.getOrAdd(":\u2208");
		_BECST = tokens.getOrAdd(":\u2223");
		_DOT = tokens.getOrAdd("\u00b7");
		_TYPING = tokens.getOrAdd("\u2982");
		_KPARTITION = tokens.getOrAdd("partition");
	}

	static int _RBRACE;
	static int _RBRACKET;
	static int _LAMBDA;
	static int _MAPSTO;
	static int _BECEQ;
	static int _BECMO;
	static int _BECST;
	static int _MID;
	public static int _KPARTITION;
	static int _DOT;
	static int _TYPING;
	public static int _PREDVAR;


	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		super.init();
		initTokens();
		
		addOpenClose("{", "}");
		addOpenClose("[", "]");
		try {
			// AssociativeExpression
			addOperator("\u222a", BUNION_ID, BINOP, new AssociativeExpressionInfix(BUNION));
			addOperator("\u2229", BINTER_ID, BINOP, new AssociativeExpressionInfix(BINTER));
			addOperator("\u2218", BCOMP_ID, BINOP, new AssociativeExpressionInfix(BCOMP));
			addOperator("\u003b", FCOMP_ID, BINOP, new AssociativeExpressionInfix(FCOMP));
			addOperator("\ue103", OVR_ID, BINOP, new AssociativeExpressionInfix(OVR));
			addOperator("+", PLUS_ID, ARITHMETIC, new AssociativeExpressionInfix(PLUS));
			addOperator("\u2217", MUL_ID, ARITHMETIC, new AssociativeExpressionInfix(MUL));
			// AssociativePredicate
			addOperator("\u2227", LAND_ID, LOGIC_PRED, new AssociativePredicateInfix(LAND));
			addOperator("\u2228", LOR_ID, LOGIC_PRED, new AssociativePredicateInfix(LOR));
			// AtomicExpression
			addOperator("\u2124", INTEGER_ID, ATOMIC_EXPR, new AtomicExpressionParser(INTEGER));
			addOperator("\u2115", NATURAL_ID, ATOMIC_EXPR, new AtomicExpressionParser(NATURAL));
			addOperator("\u21151", NATURAL1_ID, ATOMIC_EXPR, new AtomicExpressionParser(NATURAL1));
			addOperator("BOOL", BOOL_ID, ATOMIC_EXPR, new AtomicExpressionParser(Formula.BOOL));
			addOperator("TRUE", TRUE_ID, ATOMIC_EXPR, new AtomicExpressionParser(TRUE));
			addOperator("FALSE", FALSE_ID, ATOMIC_EXPR, new AtomicExpressionParser(FALSE));
			addOperator("\u2205", EMPTYSET_ID, EMPTY_SET, new AtomicExpressionParser(EMPTYSET));
			addOperator("pred", KPRED_ID, ATOMIC_EXPR, new AtomicExpressionParser(KPRED));
			addOperator("succ", KSUCC_ID, ATOMIC_EXPR, new AtomicExpressionParser(KSUCC));
			// FIXME is there a problem having the same group for V1 and V2 operators ?
			addOperator("prj1", KPRJ1_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ1, KPRJ1_GEN));
			addOperator("prj2", KPRJ2_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KPRJ2, KPRJ2_GEN));
			addOperator("id", KID_GEN_ID, ATOMIC_EXPR, new GenExpressionParser(KID, KID_GEN));
			// BecomesEqualTo	ASSIGNMENT_PARSER is called from the top
			// BecomesMemberOf	idem
			// BecomesSuchThat	idem
			// BinaryExpression
			addOperator("\u21a6", MAPSTO_ID, PAIR, new BinaryExpressionInfix(MAPSTO));
			addOperator("\u2194", REL_ID, RELATION, new BinaryExpressionInfix(REL));
			addOperator("\ue100", TREL_ID, RELATION, new BinaryExpressionInfix(TREL));
			addOperator("\ue101", SREL_ID, RELATION, new BinaryExpressionInfix(SREL));
			addOperator("\ue102", STREL_ID, RELATION, new BinaryExpressionInfix(STREL));
			addOperator("\u21f8", PFUN_ID, RELATION, new BinaryExpressionInfix(PFUN));
			addOperator("\u2192", TFUN_ID, RELATION, new BinaryExpressionInfix(TFUN));
			addOperator("\u2914", PINJ_ID, RELATION, new BinaryExpressionInfix(PINJ));
			addOperator("\u21a3", TINJ_ID, RELATION, new BinaryExpressionInfix(TINJ));
			addOperator("\u2900", PSUR_ID, RELATION, new BinaryExpressionInfix(PSUR));
			addOperator("\u21a0", TSUR_ID, RELATION, new BinaryExpressionInfix(TSUR));
			addOperator("\u2916", TBIJ_ID, RELATION, new BinaryExpressionInfix(TBIJ));
			addOperator("\u2216", SETMINUS_ID, BINOP, new BinaryExpressionInfix(SETMINUS));
			addOperator("\u00d7", CPROD_ID, BINOP, new BinaryExpressionInfix(CPROD));
			addOperator("\u2297", DPROD_ID, BINOP, new BinaryExpressionInfix(DPROD));
			addOperator("\u2225", PPROD_ID, BINOP, new BinaryExpressionInfix(PPROD));
			addOperator("\u25c1", DOMRES_ID, BINOP, new BinaryExpressionInfix(DOMRES));
			addOperator("\u2a64", DOMSUB_ID, BINOP, new BinaryExpressionInfix(DOMSUB));
			addOperator("\u25b7", RANRES_ID, BINOP, new BinaryExpressionInfix(RANRES));
			addOperator("\u2a65", RANSUB_ID, BINOP, new BinaryExpressionInfix(RANSUB));
			addOperator("\u2025", UPTO_ID, INTERVAL, new BinaryExpressionInfix(UPTO));
			addOperator("\u2212", MINUS_ID, ARITHMETIC, new BinaryExpressionInfix(MINUS));
			addOperator("\u00f7", DIV_ID, ARITHMETIC, new BinaryExpressionInfix(DIV));
			addOperator("mod", MOD_ID, ARITHMETIC, new BinaryExpressionInfix(MOD));
			addOperator("\u005e", EXPN_ID, ARITHMETIC, new BinaryExpressionInfix(EXPN));
			addOperator("(", FUNIMAGE_ID, FUNCTIONAL, new LedImage(FUNIMAGE, _RPAR));
			addOperator("[", RELIMAGE_ID, FUNCTIONAL, new LedImage(RELIMAGE, _RBRACKET));
			// BinaryPredicate
			addOperator("\u21d2", LIMP_ID, INFIX_PRED, new BinaryPredicateParser(LIMP));
			addOperator("\u21d4", LEQV_ID, INFIX_PRED, new BinaryPredicateParser(LEQV));
			// BoolExpression
			addOperator("bool", KBOOL_ID, BOOL, KBOOL_PARSER);
			// BoundIdentDecl	parsed as identifier list, then processed by parsers
			// BoundIdentifier	processed in AbstractGrammar
			// ExtendedExpression	processed in ExtendedGrammar
			// ExtendedPredicate	idem
			// FreeIdentifier	processed in AbstractGrammar
			// IntegerLiteral	idem
			// LiteralPredicate
			addOperator("\u22a4", BTRUE_ID, ATOMIC_PRED, new LiteralPredicateParser(BTRUE));
			addOperator("\u22a5", BFALSE_ID, ATOMIC_PRED, new LiteralPredicateParser(BFALSE));
			// MultiplePredicate
			addOperator("partition", KPARTITION_ID, ATOMIC_PRED, PARTITION_PARSER);
			// PredicateVariable
			addOperator(_PREDVAR, PRED_VAR_ID, GROUP0, PRED_VAR_SUBPARSER);
			// QuantifiedExpression
			addOperator("\u22c3", QUNION_ID, QUANTIFICATION, new ExplicitQuantExpr(QUNION));
			addOperator("\u22c3", QUNION_ID, QUANTIFICATION, new ImplicitQuantExpr(QUNION));
			addOperator("\u22c2", QINTER_ID, QUANTIFICATION, new ExplicitQuantExpr(QINTER));
			addOperator("\u22c2", QINTER_ID, QUANTIFICATION, new ImplicitQuantExpr(QINTER));
			addOperator("{", CSET_ID, BRACE_SETS, CSET_EXPLICIT);
			addOperator("{", CSET_ID, BRACE_SETS, CSET_IMPLICIT);
			addOperator("\u03bb", LAMBDA_ID, QUANTIFICATION, CSET_LAMBDA);
			// QuantifiedPredicate
			addOperator("\u2200", FORALL_ID, QUANTIFIED_PRED, new QuantifiedPredicateParser(FORALL));
			addOperator("\u2203", EXISTS_ID, QUANTIFIED_PRED, new QuantifiedPredicateParser(EXISTS));
			// RelationalPredicate
			addOperator("=", EQUAL_ID, RELOP_PRED, new RelationalPredicateInfix(EQUAL));
			addOperator("≠", NOTEQUAL_ID, RELOP_PRED, new RelationalPredicateInfix(NOTEQUAL));
			addOperator("<", LT_ID, RELOP_PRED, new RelationalPredicateInfix(LT));
			addOperator("≤", LE_ID, RELOP_PRED, new RelationalPredicateInfix(LE));
			addOperator(">", GT_ID, RELOP_PRED, new RelationalPredicateInfix(GT));
			addOperator("\u2265", GE_ID, RELOP_PRED, new RelationalPredicateInfix(GE));
			addOperator("\u2208", IN_ID, RELOP_PRED, new RelationalPredicateInfix(IN));
			addOperator("\u2209", NOTIN_ID, RELOP_PRED, new RelationalPredicateInfix(NOTIN));
			addOperator("\u2282", SUBSET_ID, RELOP_PRED, new RelationalPredicateInfix(SUBSET));
			addOperator("\u2284", NOTSUBSET_ID, RELOP_PRED, new RelationalPredicateInfix(NOTSUBSET));
			addOperator("\u2286", SUBSETEQ_ID, RELOP_PRED, new RelationalPredicateInfix(SUBSETEQ));
			addOperator("\u2288", NOTSUBSETEQ_ID, RELOP_PRED, new RelationalPredicateInfix(NOTSUBSETEQ));
			// SetExtension
			addOperator("{", SETEXT_ID, BRACE_SETS, SETEXT_PARSER);
			// SimplePredicate
			addOperator("finite", KFINITE_ID, ATOMIC_PRED, FINITE_PARSER);
			// UnaryExpression
			addOperator("card", KCARD_ID, FUNCTIONAL, new UnaryExpressionParser(KCARD));
			addOperator("\u2119", POW_ID, BOUND_UNARY, new UnaryExpressionParser(POW));
			addOperator("\u21191", POW1_ID, BOUND_UNARY, new UnaryExpressionParser(POW1));
			addOperator("union", KUNION_ID, BOUND_UNARY, new UnaryExpressionParser(KUNION));
			addOperator("inter", KINTER_ID, BOUND_UNARY, new UnaryExpressionParser(KINTER));
			addOperator("dom", KDOM_ID, BOUND_UNARY, new UnaryExpressionParser(KDOM));
			addOperator("ran", KRAN_ID, BOUND_UNARY, new UnaryExpressionParser(KRAN));
			addOperator("min", KMIN_ID, BOUND_UNARY, new UnaryExpressionParser(KMIN));
			addOperator("max", KMAX_ID, BOUND_UNARY, new UnaryExpressionParser(KMAX));
			addOperator("\u223c", CONVERSE_ID, UNARY_RELATION, CONVERSE_PARSER);
			addOperator("\u2212", MINUS_ID, ARITHMETIC, UNMINUS_PARSER);
			// UnaryPredicate
			addOperator("\u00ac", NOT_ID, NOT_PRED, NOT_PARSER);
			
			// Undefined Operators
			addOperator("\u2982", OFTYPE_ID, TYPED, OFTYPE);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		opRegistry.addCompatibility(MAPSTO_ID, MAPSTO_ID);
		
		opRegistry.addCompatibility(BUNION_ID, BUNION_ID);
		opRegistry.addCompatibility(BINTER_ID, BINTER_ID);
		opRegistry.addCompatibility(BINTER_ID, SETMINUS_ID);
		opRegistry.addCompatibility(BINTER_ID, RANRES_ID);
		opRegistry.addCompatibility(BINTER_ID, RANSUB_ID);
		opRegistry.addCompatibility(BCOMP_ID, BCOMP_ID);
		opRegistry.addCompatibility(FCOMP_ID, FCOMP_ID);
		opRegistry.addCompatibility(FCOMP_ID, RANRES_ID);
		opRegistry.addCompatibility(FCOMP_ID, RANSUB_ID);
		opRegistry.addCompatibility(OVR_ID, OVR_ID);
		opRegistry.addCompatibility(DOMRES_ID, BINTER_ID);
		opRegistry.addCompatibility(DOMRES_ID, SETMINUS_ID);
		opRegistry.addCompatibility(DOMRES_ID, FCOMP_ID);
		opRegistry.addCompatibility(DOMRES_ID, DPROD_ID);
		opRegistry.addCompatibility(DOMRES_ID, RANRES_ID);
		opRegistry.addCompatibility(DOMRES_ID, RANSUB_ID);
		opRegistry.addCompatibility(DOMSUB_ID, BINTER_ID);
		opRegistry.addCompatibility(DOMSUB_ID, SETMINUS_ID);
		opRegistry.addCompatibility(DOMSUB_ID, FCOMP_ID);
		opRegistry.addCompatibility(DOMSUB_ID, DPROD_ID);
		opRegistry.addCompatibility(DOMSUB_ID, RANRES_ID);
		opRegistry.addCompatibility(DOMSUB_ID, RANSUB_ID);
		opRegistry.addCompatibility(CPROD_ID, CPROD_ID); // Exception of the table  3.2
			
		
		opRegistry.addCompatibility(PLUS_ID, MINUS_ID);
		opRegistry.addCompatibility(MINUS_ID, PLUS_ID);
		opRegistry.addCompatibility(PLUS_ID, PLUS_ID);
		opRegistry.addCompatibility(MINUS_ID, MINUS_ID);
		opRegistry.addCompatibility(MUL_ID, DIV_ID);
		opRegistry.addCompatibility(MUL_ID, MOD_ID);
		opRegistry.addCompatibility(MUL_ID, MUL_ID);
		opRegistry.addCompatibility(DIV_ID, MUL_ID);
		opRegistry.addCompatibility(DIV_ID, MOD_ID);
		opRegistry.addCompatibility(MOD_ID, DIV_ID);
		opRegistry.addCompatibility(MOD_ID, MUL_ID);
		
		opRegistry.addCompatibility(CONVERSE_ID, CONVERSE_ID);

		opRegistry.addCompatibility(RELIMAGE_ID, RELIMAGE_ID);
		opRegistry.addCompatibility(FUNIMAGE_ID, FUNIMAGE_ID);
		
		opRegistry.addCompatibility(FORALL_ID, EXISTS_ID);
		opRegistry.addCompatibility(EXISTS_ID, FORALL_ID);
		
		opRegistry.addCompatibility(LAND_ID, LAND_ID);
		opRegistry.addCompatibility(LOR_ID, LOR_ID);
		
		opRegistry.addCompatibility(REL_ID, REL_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(TREL_ID, TREL_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(SREL_ID, SREL_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(STREL_ID, STREL_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(PFUN_ID, PFUN_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(TFUN_ID, TFUN_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(PINJ_ID, PINJ_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(TINJ_ID, TINJ_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(PSUR_ID, PSUR_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(TSUR_ID, TSUR_ID, LanguageVersion.V1);
		opRegistry.addCompatibility(TBIJ_ID, TBIJ_ID, LanguageVersion.V1);
		
		try {
			opRegistry.addPriority(PLUS_ID, MUL_ID);
			opRegistry.addPriority(PLUS_ID, DIV_ID);
			opRegistry.addPriority(PLUS_ID, MOD_ID);
			opRegistry.addPriority(MINUS_ID, MUL_ID);
			opRegistry.addPriority(MINUS_ID, DIV_ID);
			opRegistry.addPriority(MINUS_ID, MOD_ID);
			opRegistry.addPriority(MUL_ID, EXPN_ID);
			opRegistry.addPriority(DIV_ID, EXPN_ID);
			opRegistry.addPriority(MOD_ID, EXPN_ID);
			
			addGroupPrioritySequence(GROUP0, QUANTIFIED_PRED, INFIX_PRED,
					LOGIC_PRED, NOT_PRED, ATOMIC_PRED, RELOP_PRED, PAIR);

			addGroupPrioritySequence(GROUP0, QUANTIFICATION, RELOP_PRED);
			
//			// for OFTYPE
//			addGroupPrioritySequence(QUANTIFICATION, PAIR, TYPED, BINOP,
//					BOUND_UNARY);
			
			// start of excerpt from kernel language specification table 3.1
			addGroupPrioritySequence(QUANTIFICATION, PAIR, RELATION, BINOP,
					INTERVAL, ARITHMETIC, FUNCTIONAL, UNARY_RELATION,
					BOUND_UNARY, BOOL, BRACE_SETS);
			// end of excerpt
			
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
