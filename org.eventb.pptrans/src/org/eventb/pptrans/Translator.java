/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added translation options
 *     Systerel - added sequent translation
 *******************************************************************************/
package org.eventb.pptrans;

import static org.eventb.core.seqprover.transformer.PredicateTransformers.makeSimplifier;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.PredicateTransformers;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.core.seqprover.transformer.SimpleSequents.SimplificationOption;
import org.eventb.internal.pptrans.translator.BoundIdentifierDecomposition;
import org.eventb.internal.pptrans.translator.GoalChecker;
import org.eventb.internal.pptrans.translator.IdentifierDecomposer;
import org.eventb.internal.pptrans.translator.SequentTranslator;

/**
 * Provides access to the Translator of the Predicate Prover. The intended use
 * of this class is to call the first three provided methods in sequence, as
 * follows
 * <ol>
 * <li>{@link #decomposeIdentifiers(ISimpleSequent)}</li>
 * <li>{@link #reduceToPredicateCalulus(ISimpleSequent, Option...)}</li>
 * <li>{@link SimpleSequents#simplify(ISimpleSequent, SimplificationOption...)}</li>
 * </ol>
 * <p>
 * The translation scheme to use when reducing to predicate calculus can be
 * tailored to specific needs by providing the method with some {@link Option}
 * parameter.
 * </p>
 * <p>
 * The additional method {@link #isInGoal(ISimpleSequent)} is provided for test
 * purposes. It allows to check that the result of a translation is indeed in
 * the target sub-language.
 * </p>
 * 
 * @author Matthias Konrad
 * @author Laurent Voisin
 * @since 0.2
 */
public class Translator {

	private static final Option[] NO_OPTIONS = new Option[0];

	/**
	 * Defines translation options that can be passed to the predicate
	 * reduction method.
	 * @since 0.5
	 */
	public static enum Option {

		/**
		 * Apply rule ER9 to all set equalities.
		 */
		expandSetEquality();

	}

	private Translator() {
		// Non-instantiable class
	}

	/**
	 * Decomposes every free or bound identifier of Cartesian product type in
	 * the given sequent. These identifiers are replaced by a combination of
	 * fresh identifiers in the returned sequent. This transformation thus
	 * preserves validity, modulo some possible change to the type environment.
	 * <p>
	 * This transformation does not support mathematical extensions.
	 * </p>
	 * 
	 * @param sequent
	 *            sequent to process
	 * @return an equivalent sequent that contains no identifier of Cartesian
	 *         product type
	 * @throws UnsupportedOperationException
	 *             if some predicate of the given sequent contains a
	 *             mathematical extension operator
	 * @since 0.5
	 */
	public static ISimpleSequent decomposeIdentifiers(ISimpleSequent sequent) {
		return sequent.apply(new IdentifierDecomposer(sequent));
	}

	/**
	 * Decomposes every free or bound identifier of Cartesian product type in
	 * the given predicate.
	 * 
	 * @param predicate
	 *            the predicate whose identifiers are to be decomposed
	 * @param ff
	 *            the formula factory to use
	 * @return an equivalent predicate that contains no identifier of Cartesian
	 *         product type, except for the quantification of the free
	 *         identifiers
	 * @deprecated Use {@link #decomposeIdentifiers(ISimpleSequent)} instead
	 */
	@Deprecated
	public static Predicate decomposeIdentifiers(Predicate predicate,
			FormulaFactory ff) {
		predicate = org.eventb.internal.pptrans.translator.FreeIdentifierDecomposition
				.decomposeIdentifiers(predicate, ff);
		return BoundIdentifierDecomposition.decomposeBoundIdentifiers(
				predicate, ff);
	}

	/**
	 * Transforms a predicate from Set-Theory to Predicate Calculus. The
	 * translation scheme used is the default one, without any option enabled.
	 * 
	 * @param predicate
	 *            the predicate to reduce. This predicate must be in a
	 *            decomposed form
	 * @param ff
	 *            the formula factory to use
	 * @return a reduced predicate equivalent to the input predicate
	 * @deprecated Use
	 *             {@link #reduceToPredicateCalulus(ISimpleSequent, Option...)}
	 *             instead.
	 */
	@Deprecated
	public static Predicate reduceToPredicateCalulus(Predicate predicate,
			FormulaFactory ff) {
		return org.eventb.internal.pptrans.translator.Translator
				.reduceToPredCalc(predicate, ff, NO_OPTIONS);
	}

	/**
	 * Transforms a sequent from Set-Theory to Predicate Calculus. The
	 * translation scheme can be tailored to specific needs by providing one or
	 * several options.
	 * <p>
	 * This transformation does not support mathematical extensions. Any
	 * predicate of the given sequent that contains a mathematical extension is
	 * discarded. Such removal makes the returned sequent strictly stronger than
	 * the given sequent.
	 * </p>
	 * 
	 * @param sequent
	 *            the sequent to reduce. All predicates of the sequent must be
	 *            in a decomposed form
	 * @param options
	 *            options to be used during translation
	 * @return a reduced predicate stronger or equivalent to the input predicate
	 * @since 0.5
	 * @see #decomposeIdentifiers(ISimpleSequent)
	 */
	public static ISimpleSequent reduceToPredicateCalulus(ISimpleSequent sequent,
			Option... options) {
		return sequent.apply(new SequentTranslator(sequent, options));
	}

	/**
	 * Simplifies the given predicate using some basic simplification rules.
	 * 
	 * @param predicate
	 *            the predicate to simplify
	 * @param ff
	 *            the formula factory to use
	 * @return a simplified predicate equivalent to the input predicate
	 * @see SimpleSequents#simplify(ISimpleSequent, SimplificationOption...)
	 * @see PredicateTransformers#makeSimplifier(SimplificationOption...)
	 * @deprecated Use
	 *             {@link SimpleSequents#simplify(ISimpleSequent, SimplificationOption...)}
	 *             or
	 *             <code>predicate.rewrite(PredicateTransformers.makeSimplifier(ff))</code>
	 *             instead.
	 */
	@Deprecated
	public static Predicate simplifyPredicate(Predicate predicate,
			FormulaFactory ff) {
		return predicate.rewrite(makeSimplifier());
	}

	/**
	 * Tells whether the given predicate is in the target sub-language of the PP
	 * translator. The predicate returned by method
	 * {@link #reduceToPredicateCalulus(Predicate, FormulaFactory)} is
	 * guaranteed to pass this test.
	 * 
	 * @param predicate
	 *            a predicate to test
	 * @return <code>true</code> iff the given predicate is in the target
	 *         sub-language of the PP translator
	 * @deprecated Use {@link #isInGoal(ISimpleSequent)} instead.
	 */
	@Deprecated
	public static boolean isInGoal(Predicate predicate) {
		return GoalChecker.isInGoal(predicate);
	}

	/**
	 * Tells whether the given sequent is in the target sub-language of the PP
	 * translator.
	 * 
	 * @param sequent
	 *            a sequent to test
	 * @return <code>true</code> iff the given sequent is in the target
	 *         sub-language of the PP translator
	 * @since 0.5
	 */
	public static boolean isInGoal(ISimpleSequent sequent) {
		for (ITrackedPredicate tpred : sequent.getPredicates()) {
			final Predicate pred = tpred.getPredicate();
			if (!GoalChecker.isInGoal(pred)) {
				return false;
			}
		}
		return true;
	}

}
