/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * @author Laurent Voisin
 */
public class TrackedPredicate implements ITrackedPredicate {

	private final boolean isHypothesis;
	private final Predicate original;
	private final Predicate predicate;

	public static TrackedPredicate makeHyp(Predicate predicate) {
		return new TrackedPredicate(true, predicate);
	}

	public static TrackedPredicate makeGoal(Predicate predicate) {
		return new TrackedPredicate(false, predicate);
	}

	TrackedPredicate(boolean isHypothesis, Predicate predicate) {
		this(isHypothesis, predicate, predicate);
	}

	private TrackedPredicate(boolean isHypothesis, Predicate original,
			Predicate predicate) {
		this.isHypothesis = isHypothesis;
		this.original = original;
		this.predicate = predicate;
	}

	@Override
	public boolean isHypothesis() {
		return isHypothesis;
	}

	@Override
	public boolean holdsTrivially() {
		final int tag = predicate.getTag();
		if (isHypothesis) {
			return tag == BFALSE;
		} else {
			return tag == BTRUE;
		}
	}

	/**
	 * Returns whether this predicate contributes any information to its
	 * sequent.
	 * 
	 * @return whether this predicate is a true hypothesis or false goal
	 */
	public boolean isUseful() {
		final int tag = predicate.getTag();
		if (isHypothesis) {
			return tag != BTRUE;
		} else {
			return tag != BFALSE;
		}
	}

	@Override
	public Predicate getOriginal() {
		return original;
	}

	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Returns a new tracked predicate by applying the given transformer to this
	 * predicate.
	 * <p>
	 * In case the transformer did no transformation and returned the exact same
	 * predicate, this method returns this tracked predicate, rather than an
	 * identical copy of it.
	 * </p>
	 * 
	 * @param transformer
	 *            operation to apply to this predicate
	 * @return a possibly new tracked predicate with the same origin as this one
	 *         or <code>null</code>
	 */
	public TrackedPredicate transform(ISequentTransformer transformer) {
		final Predicate transformed = transformer.transform(this);
		if (transformed == null) {
			return null;
		}
		if (transformed == predicate) {
			return this;
		}
		return new TrackedPredicate(isHypothesis, original, transformed);
	}

	public String toString() {
		return (isHypothesis ? "hyp" : "goal") + ": " + predicate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isHypothesis ? 1231 : 1237);
		result = prime * result + predicate.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final TrackedPredicate other = (TrackedPredicate) obj;
		return this.isHypothesis == other.isHypothesis
				&& this.predicate.equals(other.predicate);
	}

}
