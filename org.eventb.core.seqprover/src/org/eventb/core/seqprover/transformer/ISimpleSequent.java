/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;

/**
 * Common protocol for representing a simple sequent, without hypotheses
 * management contrary to the sequent prover.
 * <p>
 * This interface represents a sequent reduced to just a list of hypotheses and
 * an optional goal.
 * </p>
 * <p>
 * This interface also allows access to the formula factory and type environment
 * of the sequent. Finally, some common sequent transformations are provided.
 * </p>
 * <p>
 * All predicates (hypotheses and goal) of the sequent are presented in a
 * uniform way. The sequent is guaranteed to be reduced: It does not contain any
 * true hypothesis or false goal. Moreover, if it contains a false hypothesis or
 * true goal, then this is the only predicate that it contains (see
 * {@link #getTrivialPredicate()).
 * </p>
 * <p>
 * Instances of this interface are manipulated by methods of class
 * {@link SimpleSequents}.
 * </p>
 * <p>
 * Objects implementing this interface are immutable.
 * </p>
 * 
 * @author Laurent Voisin
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.3
 * @see SimpleSequents
 */
public interface ISimpleSequent {

	/**
	 * Returns the formula factory of this sequent.
	 * 
	 * @return the formula factory of this sequent
	 */
	FormulaFactory getFormulaFactory();

	/**
	 * Returns the immutable type environment of this sequent.
	 * 
	 * @return the type environment of this sequent
	 * @since 3.0
	 */
	ISealedTypeEnvironment getTypeEnvironment();

	/**
	 * Returns the predicate causing this sequent to hold trivially, if any.
	 * <p>
	 * This sequent holds trivially iff either a hypothesis reduces to the false
	 * predicate or the goal reduces to the true predicate.
	 * </p>
	 * 
	 * @return the predicate causing this sequent to hold trivially or
	 *         <code>null</code> if this sequent does not hold trivially
	 */
	ITrackedPredicate getTrivialPredicate();

	/**
	 * Returns the predicates of this sequent, together with a trace of their
	 * origin. In the case where this sequent holds trivially, this method will
	 * return an array of exactly one element, the one returned by
	 * {@link #getTrivialPredicate()}.
	 * 
	 * @return the predicates of this sequent
	 */
	ITrackedPredicate[] getPredicates();

	/**
	 * Returns the origin of this sequent.
	 * 
	 * @return an object, or <code>null</code>
	 * @since 2.4
	 */
	Object getOrigin();

	/**
	 * Tells whether the given object is equal to this sequent, that is whether
	 * the given object is another sequent made of equal hypotheses and goal, in
	 * the same order.
	 * 
	 * @param obj
	 *            another object with which to compare.
	 * @return <code>true</code> iff the given object is considered equal to
	 *         this tracked predicate
	 * @see ITrackedPredicate#equals(Object)
	 */
	boolean equals(Object obj);

	/**
	 * Returns a new simple sequent obtained by applying the given transformer
	 * to every predicate of this sequent.
	 * <p>
	 * This methods detects whether the new sequent holds trivially. In that
	 * case, the new sequent is reduced to the single predicate that makes it
	 * holding. Additionally, predicates whose transformation becomes useless
	 * (true hypothesis or false goal) are removed from the resulting sequent.
	 * </p>
	 * <p>
	 * In case the transformer did no transformation at all and returned the
	 * exact same predicate each time, this method returns this simple sequent,
	 * rather than an identical copy of it. However, if the formula factory of
	 * the sequent changes, a new sequent built with the new formula factory is
	 * returned.
	 * </p>
	 * 
	 * @param transformer
	 *            operation to apply to each predicate of this sequent
	 * @return a new simple sequent obtained by applying the given transformer
	 *         or the same sequent if it was not changed by the transformer
	 * @see ISequentTransformer
	 * @see ISequentTranslator
	 */
	ISimpleSequent apply(ISequentTransformer transformer);

}
