/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer;

import org.eventb.core.ast.Predicate;

/**
 * Common protocol for representing a predicate that belongs to a simple sequent
 * and went through some transformation, while tracking its original form.
 * <p>
 * Objects implementing this interface are immutable.
 * </p>
 * 
 * @author Laurent Voisin
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.3
 */
public interface ITrackedPredicate {

	/**
	 * Returns whether this predicate is a hypothesis or a goal predicate in its
	 * sequent.
	 * 
	 * @return <code>true</code> if this predicate is a hypothesis,
	 *         <code>false</code> if it is a goal
	 */
	boolean isHypothesis();

	/**
	 * Returns whether this predicate makes its sequent hold trivially. There
	 * are two cases where this method returns <code>true</code> :
	 * <ul>
	 * <li>This predicate is a hypothesis and is false.</li>
	 * <li>This predicate is a goal and is true.</li>
	 * </ul>
	 * 
	 * @return whether this predicate makes its sequent hold trivially
	 */
	boolean holdsTrivially();

	/**
	 * Returns the original predicate before any modification.
	 * 
	 * @return the original predicate
	 */
	Predicate getOriginal();

	/**
	 * Returns the modified predicate.
	 * 
	 * @return the modified predicate
	 */
	Predicate getPredicate();

	/**
	 * Tells whether the given object is equal to this tracked predicate, that
	 * is whether the given object is another tracked predicate that has the
	 * same hypothesis status and carries the same predicate. The original
	 * predicate is not part of the comparison, so that two tracked predicates
	 * are considered equal even when they differ on their original predicate.
	 * 
	 * @param obj
	 *            another object with which to compare.
	 * @return <code>true</code> iff the given object is considered equal to
	 *         this tracked predicate
	 */
	@Override
	boolean equals(Object obj);

}
