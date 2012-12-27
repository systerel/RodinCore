/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_ATOMIC_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_LITERAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_MULTIPLE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_RELATIONAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_UNARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_UNARY_PREDICATE;
import static org.eventb.core.ast.Formula.KID;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPRJ1;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.PREDICATE_VARIABLE;
import static org.eventb.core.ast.LanguageVersion.V1;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.ast.tests.FastFactory.mList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Creates sets of all predicates or expressions to be used in unit tests.
 * 
 * @author franz
 */
public class Common {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	public static final BigInteger ONE = BigInteger.ONE;
	public static final BigInteger TWO = BigInteger.valueOf(2);
	public static final BigInteger FIVE = BigInteger.valueOf(5);
	public static final BigInteger MINUS_FIVE = BigInteger.valueOf(-5);
	public static final String PRED_VAR_P_NAME = PredicateVariable.LEADING_SYMBOL + "P";

	protected static final List<Integer> notV1AtomicExprTags = Arrays.asList(
			KPRJ1_GEN, KPRJ2_GEN, KID_GEN);

	@SuppressWarnings("deprecation")
	protected static final List<Integer> onlyV1UnaryTags = Arrays.asList(KPRJ1,
			KPRJ2, KID);

	public static class TagSupply {

		private static class V1TagSupply extends TagSupply {
			protected V1TagSupply() {
				super(V1);
				atomicExpressionTags.removeAll(notV1AtomicExprTags);
				multiplePredicateTags.clear();
			}
		}

		private static class V2TagSupply extends TagSupply {
			protected V2TagSupply() {
				super(V2);
				unaryExpressionTags.removeAll(onlyV1UnaryTags);
			}
		}

		public static TagSupply getAllTagSupply() {
			return new TagSupply(null);
		}

		public static TagSupply getV1TagSupply() {
			return new V1TagSupply();
		}

		public static TagSupply getV2TagSupply() {
			return new V2TagSupply();
		}

		public static TagSupply[] getAllTagSupplies() {
			return new TagSupply[] { getV1TagSupply(), getV2TagSupply() };
		}

		private static Set<Integer> setOf(int first, int length) {
			final Set<Integer> set = new LinkedHashSet<Integer>(length * 4 / 3);
			for (int i = first; i < first + length; i++) {
				set.add(i);
			}
			return set;
		}
		
		protected final LanguageVersion version;
		
		protected final Set<Integer> associativeExpressionTags = setOf(
				FIRST_ASSOCIATIVE_EXPRESSION, AssociativeExpression.TAGS_LENGTH);
		protected final Set<Integer> atomicExpressionTags = setOf(
				FIRST_ATOMIC_EXPRESSION, AtomicExpression.TAGS_LENGTH);
		protected final Set<Integer> binaryExpressionTags = setOf(
				FIRST_BINARY_EXPRESSION, BinaryExpression.TAGS_LENGTH);
		protected final Set<Integer> quantifiedExpressionTags = setOf(
				FIRST_QUANTIFIED_EXPRESSION, QuantifiedExpression.TAGS_LENGTH);
		protected final Set<Integer> unaryExpressionTags = setOf(
				FIRST_UNARY_EXPRESSION, UnaryExpression.TAGS_LENGTH);

		protected final Set<Integer> associativePredicateTags = setOf(
				FIRST_ASSOCIATIVE_PREDICATE, AssociativePredicate.TAGS_LENGTH);
		protected final Set<Integer> binaryPredicateTags = setOf(
				FIRST_BINARY_PREDICATE, BinaryPredicate.TAGS_LENGTH);
		protected final Set<Integer> literalPredicateTags = setOf(
				FIRST_LITERAL_PREDICATE, LiteralPredicate.TAGS_LENGTH);
		protected final Set<Integer> multiplePredicateTags = setOf(
				FIRST_MULTIPLE_PREDICATE, MultiplePredicate.TAGS_LENGTH);
		protected final Set<Integer> predicateVariableTags = setOf(
				PREDICATE_VARIABLE, 1);
		protected final Set<Integer> relationalPredicateTags = setOf(
				FIRST_RELATIONAL_PREDICATE, RelationalPredicate.TAGS_LENGTH);
		protected final Set<Integer> quantifiedPredicateTags = setOf(
				FIRST_QUANTIFIED_PREDICATE, QuantifiedPredicate.TAGS_LENGTH);
		protected final Set<Integer> unaryPredicateTags = setOf(
				FIRST_UNARY_PREDICATE, UnaryPredicate.TAGS_LENGTH);
		
		public TagSupply(LanguageVersion version) {
			this.version = version;
		}

	}

	/**
	 * Creates an array of all possible expressions (except bound identifiers).
	 * 
	 * @return an array of all expressions
	 */
	public static List<Expression> constructExpressions(TagSupply tagGetter) {
		final List<Expression> expressions = new ArrayList<Expression>();
		FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
		BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
		FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
		LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null);
		IntegerLiteral two = ff.makeIntegerLiteral(TWO, null);

		for (int tag : tagGetter.associativeExpressionTags) {
			expressions.add(ff.makeAssociativeExpression(tag,
					mList(id_y, id_x), null));
		}
		for (int tag : tagGetter.binaryExpressionTags) {
			expressions.add(ff.makeBinaryExpression(tag, id_x, id_x, null));
		}
		for (int tag : tagGetter.atomicExpressionTags) {
			expressions.add(ff.makeAtomicExpression(tag, null));
		}
		expressions.add(ff.makeBoolExpression(btrue, null));
		expressions.add(ff.makeIntegerLiteral(ONE, null));
		for (int tag : tagGetter.quantifiedExpressionTags) {
			expressions.add(ff.makeQuantifiedExpression(tag, mList(bd_x),
					btrue, two, null, QuantifiedExpression.Form.Explicit));
		}
		expressions.add(ff.makeSetExtension(mList(id_x), null));
		for (int tag : tagGetter.unaryExpressionTags) {
			expressions.add(ff.makeUnaryExpression(tag, id_x, null));
		}
		expressions.add(id_x);
		return expressions;
	}

	/**
	 * Creates an array of all possible predicates (except bound identifiers).
	 * 
	 * @return an array of all predicates
	 */
	public static List<Predicate> constructPredicates(TagSupply tagGetter) {
		final List<Predicate> predicates = new ArrayList<Predicate>();
		BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
		LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null);
		IntegerLiteral two = ff.makeIntegerLiteral(TWO, null);
		final FreeIdentifier id_S = ff.makeFreeIdentifier("S", null);
		final FreeIdentifier id_s = ff.makeFreeIdentifier("s", null);
		final SetExtension singleton_s = ff.makeSetExtension(id_s, null);

		for (int tag : tagGetter.binaryPredicateTags) {
			predicates.add(ff.makeBinaryPredicate(tag, btrue, btrue, null));
		}
		for (int tag : tagGetter.literalPredicateTags) {
			predicates.add(ff.makeLiteralPredicate(tag, null));
		}
		predicates.add(ff.makeSimplePredicate(Formula.KFINITE, two, null));
		for (int tag : tagGetter.quantifiedPredicateTags) {
			predicates.add(ff.makeQuantifiedPredicate(tag, mList(bd_x), btrue,
					null));
		}
		for (int tag : tagGetter.relationalPredicateTags) {
			predicates.add(ff.makeRelationalPredicate(tag, two, two, null));
		}
		for (int tag : tagGetter.unaryPredicateTags) {
			predicates.add(ff.makeUnaryPredicate(tag, btrue, null));
		}
		for (int tag : tagGetter.associativePredicateTags) {
			predicates.add(ff.makeAssociativePredicate(tag,
					mList(btrue, btrue), null));
		}
		for (int tag : tagGetter.multiplePredicateTags) {
			predicates.add(ff.makeMultiplePredicate(tag, mList(id_S,
					singleton_s), null));
		}
		predicates.add(ff.makePredicateVariable(PRED_VAR_P_NAME, null));
		return predicates;
	}

}
