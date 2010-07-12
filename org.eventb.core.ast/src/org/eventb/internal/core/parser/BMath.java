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

import static org.eventb.core.ast.AssociativeExpression.BCOMP_ID;
import static org.eventb.core.ast.AssociativeExpression.BINTER_ID;
import static org.eventb.core.ast.AssociativeExpression.BUNION_ID;
import static org.eventb.core.ast.AssociativeExpression.FCOMP_ID;
import static org.eventb.core.ast.AssociativeExpression.MUL_ID;
import static org.eventb.core.ast.AssociativeExpression.OVR_ID;
import static org.eventb.core.ast.AssociativeExpression.PLUS_ID;
import static org.eventb.core.ast.AssociativePredicate.LAND_ID;
import static org.eventb.core.ast.AssociativePredicate.LOR_ID;
import static org.eventb.core.ast.BinaryExpression.CPROD_ID;
import static org.eventb.core.ast.BinaryExpression.DIV_ID;
import static org.eventb.core.ast.BinaryExpression.DOMRES_ID;
import static org.eventb.core.ast.BinaryExpression.DOMSUB_ID;
import static org.eventb.core.ast.BinaryExpression.DPROD_ID;
import static org.eventb.core.ast.BinaryExpression.EXPN_ID;
import static org.eventb.core.ast.BinaryExpression.FUNIMAGE_ID;
import static org.eventb.core.ast.BinaryExpression.MAPSTO_ID;
import static org.eventb.core.ast.BinaryExpression.MINUS_ID;
import static org.eventb.core.ast.BinaryExpression.MOD_ID;
import static org.eventb.core.ast.BinaryExpression.RANRES_ID;
import static org.eventb.core.ast.BinaryExpression.RANSUB_ID;
import static org.eventb.core.ast.BinaryExpression.RELIMAGE_ID;
import static org.eventb.core.ast.BinaryExpression.SETMINUS_ID;
import static org.eventb.core.ast.QuantifiedPredicate.EXISTS_ID;
import static org.eventb.core.ast.QuantifiedPredicate.FORALL_ID;
import static org.eventb.core.ast.UnaryExpression.CONVERSE_ID;
import static org.eventb.internal.core.parser.OperatorRegistry.GROUP0;
import static org.eventb.internal.core.parser.SubParsers.OFTYPE;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 */
public abstract class BMath extends AbstractGrammar {
	
	private final LanguageVersion version;
	
	protected BMath(LanguageVersion version) {
		this.version = version;
	}
	
	public LanguageVersion getVersion() {
		return version;
	}
	
	public static final String RELOP_PRED = "Relational Operator Predicate";
	public static final String QUANTIFICATION = "Quantification";
	public static final String PAIR = "Pair";
	public static final String RELATION = "Set of Relations";
	public static final String BINOP = "Binary Operator";
	public static final String INTERVAL = "Interval";
	public static final String ARITHMETIC = "Arithmetic";
	public static final String UNARY_RELATION = "Unary Relation";
	private static final String TYPED = "Typed";
	public static final String FUNCTIONAL = "Functional";
	public static final String BRACE_SETS = "Brace Sets";
	public static final String QUANTIFIED_PRED = "Quantified";
	public static final String LOGIC_PRED = "Logic Predicate";
	public static final String INFIX_PRED = "Infix Predicate";
	public static final String NOT_PRED = "Not Predicate";
	public static final String ATOMIC_PRED = "Atomic Predicate";
	public static final String ATOMIC_EXPR = "Atomic Expression";
	public static final String BOUND_UNARY = "Bound Unary";
	public static final String BOOL_EXPR = "Bool";
	public static final String INFIX_SUBST = "Infix Substitution";
	
	private static final String OFTYPE_ID = "Oftype";
	
	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	private final void initTokens() {
		_RBRACKET = tokens.getOrAdd("]");
		_RBRACE = tokens.getOrAdd("}");
		_MAPSTO = tokens.getOrAdd("\u21a6");
		_MID = tokens.getOrAdd("\u2223");
		_DOT = tokens.getOrAdd("\u00b7");
		_TYPING = tokens.getOrAdd("\u2982");
		_KPARTITION = tokens.getOrAdd("partition");
	}

	static int _RBRACE;
	public static int _RBRACKET;
	static int _MAPSTO;
	static int _MID;
	public static int _KPARTITION;
	static int _DOT;
	static int _TYPING;
	
	public static final int _PREDVAR = publicTokens.reserved("Predicate Variable");


	@Override
	protected void addOperators() {
		initTokens();
		
		addOpenClose("{", "}");
		addOpenClose("[", "]");
		try {
			// AssociativeExpression
			AssociativeExpression.init(this);
			// AssociativePredicate
			AssociativePredicate.init(this);
			// AtomicExpression is version specific
			// BecomesEqualTo
			BecomesEqualTo.init(this);
			// BecomesMemberOf
			BecomesMemberOf.init(this);
			// BecomesSuchThat
			BecomesSuchThat.init(this);
			// BinaryExpression
			BinaryExpression.init(this);
			// BinaryPredicate
			BinaryPredicate.init(this);
			// BoolExpression
			BoolExpression.init(this);
			// BoundIdentDecl	parsed as identifier list, then processed by parsers
			// BoundIdentifier	processed in AbstractGrammar
			// ExtendedExpression	processed in ExtendedGrammar
			// ExtendedPredicate	idem
			// FreeIdentifier	processed in AbstractGrammar
			// IntegerLiteral	idem
			// LiteralPredicate
			LiteralPredicate.init(this);
			// MultiplePredicate is V2 specific
			// PredicateVariable
			PredicateVariable.init(this);
			// QuantifiedExpression
			QuantifiedExpression.init(this);
			// QuantifiedPredicate
			QuantifiedPredicate.init(this);
			// RelationalPredicate
			RelationalPredicate.init(this);
			// SetExtension
			SetExtension.init(this);
			// SimplePredicate
			SimplePredicate.init(this);
			// UnaryExpression is version specific
			// UnaryPredicate
			UnaryPredicate.init(this);
			
			// Undefined Operators
			addOperator("\u2982", OFTYPE_ID, TYPED, OFTYPE);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	protected void addOperatorRelationships() {
		addCompatibility(MAPSTO_ID, MAPSTO_ID);
		
		addCompatibility(BUNION_ID, BUNION_ID);
		addCompatibility(BINTER_ID, BINTER_ID);
		addCompatibility(BINTER_ID, SETMINUS_ID);
		addCompatibility(BINTER_ID, RANRES_ID);
		addCompatibility(BINTER_ID, RANSUB_ID);
		addCompatibility(BCOMP_ID, BCOMP_ID);
		addCompatibility(FCOMP_ID, FCOMP_ID);
		addCompatibility(FCOMP_ID, RANRES_ID);
		addCompatibility(FCOMP_ID, RANSUB_ID);
		addCompatibility(OVR_ID, OVR_ID);
		addCompatibility(DOMRES_ID, BINTER_ID);
		addCompatibility(DOMRES_ID, SETMINUS_ID);
		addCompatibility(DOMRES_ID, FCOMP_ID);
		addCompatibility(DOMRES_ID, DPROD_ID);
		addCompatibility(DOMRES_ID, RANRES_ID);
		addCompatibility(DOMRES_ID, RANSUB_ID);
		addCompatibility(DOMSUB_ID, BINTER_ID);
		addCompatibility(DOMSUB_ID, SETMINUS_ID);
		addCompatibility(DOMSUB_ID, FCOMP_ID);
		addCompatibility(DOMSUB_ID, DPROD_ID);
		addCompatibility(DOMSUB_ID, RANRES_ID);
		addCompatibility(DOMSUB_ID, RANSUB_ID);
		addCompatibility(CPROD_ID, CPROD_ID); // Exception of the table  3.2
			
		
		addCompatibility(PLUS_ID, MINUS_ID);
		addCompatibility(MINUS_ID, PLUS_ID);
		addCompatibility(PLUS_ID, PLUS_ID);
		addCompatibility(MINUS_ID, MINUS_ID);
		addCompatibility(MUL_ID, DIV_ID);
		addCompatibility(MUL_ID, MOD_ID);
		addCompatibility(MUL_ID, MUL_ID);
		addCompatibility(DIV_ID, MUL_ID);
		addCompatibility(DIV_ID, MOD_ID);
		addCompatibility(MOD_ID, DIV_ID);
		addCompatibility(MOD_ID, MUL_ID);
		
		addCompatibility(CONVERSE_ID, CONVERSE_ID);

		addCompatibility(RELIMAGE_ID, RELIMAGE_ID);
		addCompatibility(FUNIMAGE_ID, FUNIMAGE_ID);
		
		addCompatibility(FORALL_ID, EXISTS_ID);
		addCompatibility(EXISTS_ID, FORALL_ID);
		
		addCompatibility(LAND_ID, LAND_ID);
		addCompatibility(LOR_ID, LOR_ID);
		
		try {
			addPriority(PLUS_ID, MUL_ID);
			addPriority(PLUS_ID, DIV_ID);
			addPriority(PLUS_ID, MOD_ID);
			addPriority(MINUS_ID, MUL_ID);
			addPriority(MINUS_ID, DIV_ID);
			addPriority(MINUS_ID, MOD_ID);
			addPriority(MUL_ID, EXPN_ID);
			addPriority(DIV_ID, EXPN_ID);
			addPriority(MOD_ID, EXPN_ID);
			
			addGroupPrioritySequence(GROUP0, QUANTIFIED_PRED, INFIX_PRED,
					LOGIC_PRED, NOT_PRED, ATOMIC_PRED, RELOP_PRED, PAIR);

			addGroupPrioritySequence(GROUP0, QUANTIFICATION, RELOP_PRED);
			
			// start of excerpt from kernel language specification table 3.1
			addGroupPrioritySequence(QUANTIFICATION, PAIR, RELATION, BINOP,
					INTERVAL, ARITHMETIC, FUNCTIONAL, UNARY_RELATION,
					BOUND_UNARY, BOOL_EXPR, BRACE_SETS);
			// end of excerpt
			
			// ATOMIC_EXPR has the highest priority
			addGroupPrioritySequence(PAIR, ATOMIC_EXPR);
			addGroupPrioritySequence(BRACE_SETS, ATOMIC_EXPR);
			
			// for OFTYPE
			addGroupPrioritySequence(PAIR, TYPED, RELATION);
			
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
