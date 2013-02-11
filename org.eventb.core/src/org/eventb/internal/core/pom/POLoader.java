/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added formula factory parameter
 *     Systerel - changed condition for including WD predicates
 *     Systerel - set the origin of the resulting prover sequent
 *******************************************************************************/
package org.eventb.internal.core.pom;

import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.seqprover.ProverFactory.makeSequent;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.Util;
import org.rodinp.core.RodinDBException;

/**
 * This class handles loading and generation of Prover Sequents from PO sequents
 * stored in the database.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 *
 */
public final class POLoader {
	
	// flag for debug trace 
	public static boolean DEBUG = false;

	private POLoader() {
		super();
	}

	/**
	 * Returns the sequent associated to the given proof obligation.
	 * <p>
	 * The PO file containing the proof obligation to read should be locked
	 * before running this method, so that the PO doesn't change while reading
	 * it.
	 * </p>
	 * 
	 * @param poSeq
	 *            the proof obligation to read
	 * @param factory
	 *            the formula factory to use
	 * @return the sequent of the given proof obligation
	 * @throws RodinDBException
	 */
	public static IProverSequent readPO(IPOSequent poSeq, FormulaFactory factory)
			throws RodinDBException {
		final ITypeEnvironmentBuilder typeEnv = factory.makeTypeEnvironment();
		final Set<Predicate> hypotheses = new LinkedHashSet<Predicate>();
		final Set<Predicate> selHyps = new LinkedHashSet<Predicate>();
		final SelectionHints selHints = new SelectionHints(poSeq);
		loadHypotheses(poSeq, selHints, hypotheses, selHyps, typeEnv, factory);
		final Predicate goal = readGoal(poSeq, typeEnv, factory);
		if (! isWDPO(poSeq)) addWDpredicates(goal, hypotheses, factory);
		return makeSequent(typeEnv, hypotheses, selHyps, goal, poSeq);
	}
	
	/**
	 * Checks if the given {@link IPOSequent} is a proof obligation for proving
	 * WD of the goal.
	 * 
	 * <p>
	 * In this case, adding the WD predicate of the goal to the hypotheses can
	 * be avoided (see {@link #readPO(IPOSequent, FormulaFactory)}).
	 * </p>
	 * 
	 * 
	 * @param poSeq
	 *            the sequent to check for
	 * @return <code>true</code> iff the given PO is a WD PO.
	 * 
	 * 
	 */
	private static boolean isWDPO(IPOSequent poSeq){
		return (poSeq.getElementName().endsWith("/WD"));
	}

	/**
	 * Loads the hypotheses of the given PO and appends them to the given set of
	 * hypotheses. The given type environment is enriched with the types of the
	 * free identifiers that occur in the loaded hypotheses.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param selHints 
	 * 			  information regarding hypotheses selection
	 * @param hypotheses
	 *            set of hypotheses where to store the loaded hypotheses
	 * @param selHyps 
	 *            set of hypotheses where to store the selected loaded hypotheses
	 * @param typeEnv
	 *            type environment to enrich at the same time
	 * @param factory
	 *            the formula factory to use
	 * @throws RodinDBException
	 */
	private static void loadHypotheses(IPOSequent poSeq,
			SelectionHints selHints, Set<Predicate> hypotheses,
			Set<Predicate> selHyps, ITypeEnvironmentBuilder typeEnv,
			FormulaFactory factory) throws RodinDBException {

		IPOPredicateSet[] dbHyps = poSeq.getHypotheses();
		if (dbHyps.length == 0) {
			Util.log(null, "No predicate set in PO " + poSeq);
			return;
		}
		if (dbHyps.length != 1) {
			Util.log(null, "More than one predicate set in PO " + poSeq);
		}
		loadPredicateSet(dbHyps[0], selHints, hypotheses, selHyps, typeEnv,
				factory);
	}
	
	private static void loadPredicateSet(IPOPredicateSet poPredSet,
			SelectionHints selHints, Set<Predicate> hypotheses,
			Set<Predicate> selHyps, ITypeEnvironmentBuilder typeEnv,
			FormulaFactory factory) throws RodinDBException {

		final IPOPredicateSet parentSet = poPredSet.getParentPredicateSet();
		if (parentSet != null) {
			loadPredicateSet(parentSet, selHints, hypotheses, selHyps, typeEnv,
					factory);
		}
		for (final IPOIdentifier poIdent: poPredSet.getIdentifiers()) {
			typeEnv.add(poIdent.getIdentifier(factory));
		}
		
		boolean selected = selHints.contains(poPredSet);
		for (final IPOPredicate poPred : poPredSet.getPredicates()) {
			final Predicate predicate = poPred.getPredicate(typeEnv);
			final Predicate hypothesis = predicate;
			if ( selected || selHints.contains(poPred)) selHyps.add(hypothesis);
			hypotheses.add(hypothesis);
			addWDpredicates(hypothesis, hypotheses, factory);
		}
	}
	
	/**
	 * Reads the goal of the given proof obligation.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param typeEnv
	 *            type environment to use
	 * @param factory
	 *            the formula factory to use
	 * @return the goal of the given PO
	 * @throws RodinDBException
	 */
	private static Predicate readGoal(IPOSequent poSeq,
			ITypeEnvironment typeEnv, FormulaFactory factory)
			throws RodinDBException {
		
		IPOPredicate[] dbGoals = poSeq.getGoals();
		if (dbGoals.length == 0) {
			Util.log(null, "No goal for PO " + poSeq);
			return null;
		}
		if (dbGoals.length != 1) {
			Util.log(null, "More than one goal for PO " + poSeq);
		}
		return dbGoals[0].getPredicate(typeEnv);
	}
	

	/**
	 * Adds the WD predicates of the given predicate to the given predicate set
	 * in case it should be added (see {@link #shouldWDpredBeAdded(Predicate)}).
	 * 
	 * <p>
	 * In case the WD predicate is 'true', this is not added. In case the WD
	 * predicate is a conjunction, its conjuncts are added.
	 * </p>
	 * 
	 * @param pred
	 *            The predicate whose WD predicate should be added to the given
	 *            predicate set
	 * @param ff
	 *            the formula factory to use
	 * @param predSet
	 *            The predicate set to which this WD predicate should be added
	 */
	private static void addWDpredicates(Predicate pred, Set<Predicate> predSet,
			FormulaFactory ff) {
		if (! shouldWDpredBeAdded(pred)) return;
		Set<Predicate> toAdd = breakPossibleConjunct(pred.getWDPredicate());
		toAdd.remove(ff.makeLiteralPredicate(Formula.BTRUE, null));
		predSet.addAll(toAdd);
	}

	/**
	 * Filter for WD predicates that are considered uninteresting. Currently, we
	 * filter out any predicate that contains a universal predicate.
	 */
	private static class NoForall implements IFormulaInspector<Boolean> {

		public static boolean containsForall(Predicate pred) {
			final NoForall inspector = new NoForall();
			pred.inspect(inspector);
			return inspector.wasFound();
		}

		// True if a universal predicate has been found
		private boolean found = false;

		private boolean wasFound() {
			return found;
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<Boolean> accumulator) {
			// Go to children
		}

		@Override
		public void inspect(AtomicExpression expression,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			// Go to children
		}

		@Override
		public void inspect(BoolExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(BoundIdentDecl decl,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(ExtendedExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(ExtendedPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(MultiplePredicate predicate,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(PredicateVariable predicate,
				IAccumulator<Boolean> accumulator) {
			// Do nothing
		}

		@Override
		public void inspect(QuantifiedExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(QuantifiedPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			if (predicate.getTag() == FORALL) {
				accumulator.skipAll();
				found = true;
			}
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(SetExtension expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(SimplePredicate predicate,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<Boolean> accumulator) {
			accumulator.skipChildren();
		}

		@Override
		public void inspect(UnaryPredicate predicate,
				IAccumulator<Boolean> accumulator) {
			// Go to child
		}

	}

	/**
	 * Filters predicates for whom WD predicates should be added to the
	 * hypotheses.
	 * <p>
	 * The condition used here is that the WD predicate shall not contain any
	 * universal quantification, because such predicates have a very low
	 * probability of being useful.
	 * </p>
	 * 
	 * @param pred
	 *            the predicate to filter
	 * @return <code>true</code> iff the WD predicate for this predicate should
	 *         be added to the hypotheses.
	 * 
	 * @see #addWDpredicates(Predicate, Set, FormulaFactory)
	 */
	private static boolean shouldWDpredBeAdded(Predicate pred) {
		return !NoForall.containsForall(pred);
	}
	
	/**
	 * This is a private class that collects selection hints from a PO Sequent.
	 * The collected hints can then be used for querying whether a predicate or
	 * predicate set should be selected.
	 * <p>
	 * The selection hints are collected as handles to PO Predicates, and handles 
	 * to PO Predicate Sets that are contained in the selection hints for a 
	 * PO Sequent.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 */
	private static class SelectionHints {
		
		private Set<IPOPredicate> preds;
		private Set<IPOPredicateSet> predSets;
		
		
		/**
		 * Collects Selection hint information from a PO sequent
		 * 
		 * @param poSeq
		 * 			The PO sequent to collect hints from
		 * @throws RodinDBException
		 */
		protected SelectionHints(IPOSequent poSeq) throws RodinDBException {
			preds = new HashSet<IPOPredicate>();
			predSets = new HashSet<IPOPredicateSet>();
			IPOSelectionHint[] dbSelHints = poSeq.getSelectionHints();
			for (IPOSelectionHint selectionHint : dbSelHints) {
				
				IPOPredicateSet endP = selectionHint.getEnd();
				 if (endP == null) {
					 IPOPredicate pred = selectionHint.getPredicate();
					 preds.add(pred);
				 	} else {
				 		IPOPredicateSet startP = selectionHint.getStart();
				 		while (!endP.equals(startP)) {
				 			predSets.add(endP);
				 			endP = endP.getParentPredicateSet();
				 		}
				 	}
			}
		}
		
		
		/**
		 * A query on the collected selection hints.
		 * 
		 * @param dbPred
		 * 		the poPredicate handle to query for
		 * @return <code>true</code> iff the given poPredicate handle is contained
		 * 			in the collected selection hints.
		 */
		protected boolean contains(IPOPredicate dbPred){
			return preds.contains(dbPred);
		}
		
		/**
		 * A query on the collected selection hints.
		 * 
		 * @param dbPredSet
		 * 		the poPredicateSet handle to query for
		 * @return <code>true</code> iff the given poPredicateSet handle is contained
		 * 			in the collected selection hints.
		 */	
		protected boolean contains(IPOPredicateSet dbPredSet){
			return predSets.contains(dbPredSet);
		}
		
	}

}
