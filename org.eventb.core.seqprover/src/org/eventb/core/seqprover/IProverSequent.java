/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extended interface with getFormulaFactory() method
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.IInternalProverSequent;

/**
 * Common protocol for reading Prover Sequents.
 * 
 * <p>
 * A sequent contains:
 * <ul>
 * <li> A <em>goal</em> : the predicate to be proven.
 * <li> A set of <em>hypotheses</em>: predicates that can be used 
 * to prove the goal.
 * <li> A <em>type environment</em> containing all free identifiers 
 * and carrier sets appearing in the goal and hypotheses.
 * </ul>
 * The fact that all predicates in the sequent share a common type environment ensures
 * that every free identifier has a unique type and makes the search for fresh
 * free identifiers easier.
 * </p>
 * 
 * <p>
 * In addition to the explicitly stated set of hypotheses, the well-definedness 
 * predicates for each hypothesis and the goal may be assumed to prove the goal.
 * </p>
 * 
 * <p>
 * The set of hypotheses contains two subsets:
 * <ul>
 * <li> <em>Selected</em> hypotheses : hypotheses thought to be relevant while proving
 * the goal.
 * <li> <em>Hidden</em> hypotheses : hypotheses thought to be strongly irrelevant,
 * redundant, or misleading while proving the goal. 
 * </ul>
 * These two subsets are disjoint and need not contain all hypotheses. These
 * subsets have no logical significance. Their sole purpose is to add structure
 * to sequents with a large number of hypotheses. Their sizes are intended to be
 * smaller in comparason the set of all hypotheses.
 * </p>
 * 
 * 
 * <p>
 * Prover sequents are implemented as immutable. They are created using factory methods
 * in {@link ProverFactory}.
 * </p>
 * <p>
 * To avoid needless copying, modifications to prover sequents are treated internally
 * as incremental constructions. 
 * The methods used for incrementally constructing new prover sequents from existing
 * ones are not currently exported, but can be found in {@link IInternalProverSequent}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 * @noimplement
 * @noextend
 * @since 1.0
 */
public interface IProverSequent{
	
	/**
	 * Returns the type environment for the sequent.
	 * 
	 * <p>
	 * This type environment contains all free identifiers and carrier
	 * sets appearing in the sequent and can be used to successfully
	 * type check all predicates appearing in the sequent.
	 * </p>
	 * 
	 * @return the type environment of the sequent
	 */
	ITypeEnvironment typeEnvironment();

	
	/**
	 * Returns an iterator for all hypotheses of the sequent.
	 * <p>
	 * This iterator returns hypotheses in the same order as this set was 
	 * constructed.
	 * </p>
	 * <p>
	 * It is intended to be used in the following way:<br>
	 * <code>
	 * for (Predicate hyp : seq.hypIterable())<br>
	 * {
	 * 		// do something with hyp <br>
	 * }
	 * </code>
	 * </p>
	 * 
	 * @return an iterator for all hypotheses occuring in the sequent
	 */
	Iterable<Predicate> hypIterable();
	
	/**
	 * Searches for the given predicate in the set of hypotheses.
	 * 
	 * @param pred
	 * 		The predicate to search for.
	 * @return <code>true</code> iff the given predicate is a hypothesis
	 * 	of this sequent.
	 */
	boolean containsHypothesis(Predicate pred);
	
	/**
	 * Searches for the given predicates in the set of hypotheses.
	 * 
	 * @param preds
	 * 		The predicates to search for.
	 * @return <code>true</code> iff all the given predicates are hypotheses
	 * 	of this sequent.
	 */
	boolean containsHypotheses(Collection<Predicate> preds);
	
	
	/**
	 * Returns the goal predicate of this sequent.
	 * 
	 * @return the goal predicate of this sequent.
	 */
	Predicate goal();
	
	
	/**
	 * Searches for the given predicate in the set of selected hypotheses.
	 * 
	 * @param pred
	 * 		The predicate to search for.
	 * @return <code>true</code> iff the given predicate is a selected hypothesis
	 * 	of this sequent.
	 */
	boolean isSelected(Predicate pred);
	
	
	/**
	 * Returns an iterator for all selected hypotheses of this sequent.
	 * <p>
	 * This iterator mantains the order of selected hypotheses. This order is 
	 * the order in which hypotheses are selected. In case a hypothesis is selected
	 * a second time, its order does not get modified.
	 * </p>
	 * <p>
	 * It is intended to be used in the following way:<br>
	 * <code>
	 * for (Predicate hyp : seq.selectedHypIterable())<br>
	 * {
	 * 		// do something with hyp <br>
	 * }
	 * </code>
	 * </p>
	 * 
	 * 
	 * @return an iterator for all selected hypotheses of this sequent
	 */
	Iterable<Predicate> selectedHypIterable();
	
	/**
	 * Searches for the given predicate in the set of hidden hypotheses.
	 * 
	 * @param pred
	 * 		The predicate to search for.
	 * @return <code>true</code> iff the given predicate is a hidden hypothesis
	 * 	of this sequent.
	 */
	boolean isHidden(Predicate pred);
	
	/**
	 * Returns an iterator for all hidden hypotheses of this sequent.
	 * <p>
	 * This iterator returns hypotheses in the same order as this set was 
	 * constructed.
	 * </p>
	 * <p>
	 * It is intended to be used in the following way:<br>
	 * <code>
	 * for (Predicate hyp : seq.hiddenHypIterable())<br>
	 * {
	 * 		// do something with hyp <br>
	 * }
	 * </code>
	 * </p>
	 * 
	 * @return an iterator for all hidden hypotheses of this sequent
	 */
	Iterable<Predicate> hiddenHypIterable();
	
	/**
	 * Returns an iterator for all visible hypotheses of this sequent.
	 * <p>
	 * This iterator returns hypotheses in the same order as this set was 
	 * constructed.
	 * </p> 
	 * <p>
	 * It is intended to be used in the following way:<br>
	 * <code>
	 * for (Predicate hyp : seq.visibleHypIterable())<br>
	 * {
	 * 		// do something with hyp <br>
	 * }
	 * </code>
	 * </p>
	 * 
	 * @return an iterator for all visible hypotheses of this sequent
	 */
	Iterable<Predicate> visibleHypIterable();
	
	/**
	 * Returns the formula factory to use with this sequent.
	 * 
	 * @return the formula factory to use
	 */
	FormulaFactory getFormulaFactory();
	
}
