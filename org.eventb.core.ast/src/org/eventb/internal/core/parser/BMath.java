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
import static org.eventb.core.ast.UnaryPredicate.NOT_ID;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ARITHMETIC;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ATOMIC_EXPR;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ATOMIC_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.BINOP;
import static org.eventb.internal.core.parser.BMath.StandardGroup.BOOL_EXPR;
import static org.eventb.internal.core.parser.BMath.StandardGroup.BRACE_SETS;
import static org.eventb.internal.core.parser.BMath.StandardGroup.CLOSED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.FUNCTIONAL;
import static org.eventb.internal.core.parser.BMath.StandardGroup.GROUP_0;
import static org.eventb.internal.core.parser.BMath.StandardGroup.INFIX_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.INTERVAL;
import static org.eventb.internal.core.parser.BMath.StandardGroup.LOGIC_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.NOT_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.PAIR;
import static org.eventb.internal.core.parser.BMath.StandardGroup.QUANTIFICATION;
import static org.eventb.internal.core.parser.BMath.StandardGroup.QUANTIFIED_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.RELATION;
import static org.eventb.internal.core.parser.BMath.StandardGroup.RELOP_PRED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.TYPED;
import static org.eventb.internal.core.parser.BMath.StandardGroup.UNARY_RELATION;

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
import org.eventb.internal.core.ast.ASTPlugin;

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

	public static enum StandardGroup {// TODO externalize
		GROUP_0("group0", "Least Priority Group"),
		RELOP_PRED("relOp", "Relational Operator Predicate"),
		QUANTIFICATION("quantification", "Quantification"),
		PAIR("pair", "Pair"),
		RELATION("relation", "Set of Relations"),
		BINOP("binOp", "Binary Operator"),
		INTERVAL("interval", "Interval"),
		ARITHMETIC("arithmetic", "Arithmetic"),
		UNARY_RELATION("unaryRelation", "Unary Relation"),
		TYPED("typed", "Typed"),
		FUNCTIONAL("functional", "Functional"),
		BRACE_SETS("braceSets", "Brace Sets"),
		QUANTIFIED_PRED("quantifiedPred", "Quantified"),
		LOGIC_PRED("logicPred", "Logic Predicate"),
		INFIX_PRED("infixPred", "Infix Predicate"),
		NOT_PRED("notPred", "Not Predicate"),
		ATOMIC_PRED("atomicPred", "Atomic Predicate"),
		ATOMIC_EXPR("atomicExpr", "Atomic Expression"),
		CLOSED("closed", "Closed"),
		BOOL_EXPR("boolExpr", "Bool"),
		INFIX_SUBST("infixSubst", "Infix Substitution"),
		;
		private final String id;
		private final String name;
		private StandardGroup(String bareId, String name) {
			this.id = ASTPlugin.PLUGIN_ID + "." + bareId;
			this.name = name;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
	}
	
	@Override
	protected void addOperators() {
		
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

	}
	
	@Override
	protected void addOperatorRelationships() {
		// MAPSTO is compatible with itself but not associative 
		// => no parentheses are required for printing (a |-> b) |-> c
		addCompatibility(MAPSTO_ID, MAPSTO_ID);
		
		// BUNION is compatible with itself and associative (as others below)
		// => parentheses are required for printing (a \/ b) \/ c
		addAssociativity(BUNION_ID);
		addAssociativity(BINTER_ID);
		addCompatibility(BINTER_ID, SETMINUS_ID);
		addCompatibility(BINTER_ID, RANRES_ID);
		addCompatibility(BINTER_ID, RANSUB_ID);
		addAssociativity(BCOMP_ID);
		addAssociativity(FCOMP_ID);
		addCompatibility(FCOMP_ID, RANRES_ID);
		addCompatibility(FCOMP_ID, RANSUB_ID);
		addAssociativity(OVR_ID);
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
		// CPROD has the same properties as MAPSTO, defined above
		
		addCompatibility(PLUS_ID, MINUS_ID);
		addCompatibility(MINUS_ID, PLUS_ID);
		addAssociativity(PLUS_ID);
		addCompatibility(MINUS_ID, MINUS_ID);
		addCompatibility(MUL_ID, DIV_ID);
		addCompatibility(MUL_ID, MOD_ID);
		addAssociativity(MUL_ID);
		addCompatibility(DIV_ID, MUL_ID);
		addCompatibility(DIV_ID, MOD_ID);
		addCompatibility(MOD_ID, DIV_ID);
		addCompatibility(MOD_ID, MUL_ID);
		addCompatibility(NEGLIT_ID, PLUS_ID);
		addCompatibility(NEGLIT_ID, MINUS_ID);
		
		// CONVERSE is compatible with itself but not associative (meaningless)
		// => parentheses are required for printing r~~
		addCompatibility(CONVERSE_ID, CONVERSE_ID);

		// same as CONVERSE (prevents over parenthesizing)
		addCompatibility(RELIMAGE_ID, RELIMAGE_ID);
		addCompatibility(FUNIMAGE_ID, FUNIMAGE_ID);
		
		addCompatibility(FORALL_ID, EXISTS_ID);
		addCompatibility(EXISTS_ID, FORALL_ID);
		
		addAssociativity(LAND_ID);
		addAssociativity(LOR_ID);
		
		addCompatibility(NOT_ID, NOT_ID);
		
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
			
			addGroupPrioritySequence(GROUP_0, QUANTIFIED_PRED, INFIX_PRED,
					LOGIC_PRED, NOT_PRED, ATOMIC_PRED, RELOP_PRED, PAIR);

			addGroupPrioritySequence(GROUP_0, QUANTIFICATION, RELOP_PRED);
			
			// start of excerpt from kernel language specification table 3.1
			addGroupPrioritySequence(QUANTIFICATION, PAIR, RELATION, BINOP,
					INTERVAL, ARITHMETIC, FUNCTIONAL, UNARY_RELATION,
					CLOSED, BOOL_EXPR, BRACE_SETS);
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
}
