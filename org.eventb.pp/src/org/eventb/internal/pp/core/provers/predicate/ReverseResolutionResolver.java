/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.iterators.UnitMatchIterable;

/**
 * This class is responsible for applying the unit resolution rule between a
 * clause and a set of unit clauses. It is instantiated with a {@link UnitMatchIterable}
 * and initialized with a non-unit clause. Each call to {@link #next()} then 
 * returns a match between the non-unit clause and the unit clauses in the 
 * {@link UnitMatchIterable}.
 *
 * @author François Terrier
 *
 */
public class ReverseResolutionResolver implements IResolver {

	private ResolutionInferrer inferrer;
	private UnitMatchIterable nonUnitProver;
	
	public ReverseResolutionResolver(ResolutionInferrer inferrer, UnitMatchIterable nonUnitProver) {
		this.inferrer = inferrer;
		this.nonUnitProver = nonUnitProver;
	}
	
	private int currentPosition;
	private Clause currentNonUnit;
	private Clause currentUnit;
	private Iterator<Clause> currentMatchIterator;
	
	@Override
	public ResolutionResult next() {
		if (setUnit()) return doMatch();
		while (setNonUnit()) {
			initMatchIterator();
			if (setUnit()) {
				return doMatch();
			}
		}
		return null;
	}
	
	@Override
	public void initialize(Clause nonUnitClause) {
		this.currentNonUnit = nonUnitClause;
		currentPosition = -1;
		currentMatchIterator = null;
	}
	
	@Override
	public boolean isInitialized() {
		return currentNonUnit != null;
	}

	private boolean setUnit() {
		if (currentMatchIterator == null) return false;
		while (currentMatchIterator.hasNext()) {
			currentUnit = currentMatchIterator.next();
			PredicateLiteral literal = currentUnit.getPredicateLiteral(0);
			if (currentNonUnit.matchesAtPosition(literal.getDescriptor(), literal.isPositive(), currentPosition)) {
				return true;
			}
		}
		return false;
	}
	
	private ResolutionResult doMatch() {
		inferrer.setPosition(currentPosition);
		inferrer.setUnitClause(currentUnit);
		currentNonUnit.infer(inferrer);
		ResolutionResult result = new ResolutionResult(inferrer.getDerivedClause(), inferrer.getSubsumedClause());
		if (PredicateProver.DEBUG) PredicateProver.debug("Inferred clause: "+currentUnit+" + "+currentNonUnit+" -> "+result.getDerivedClause());
		return result;
	}

	private void initMatchIterator() {
		PredicateLiteral predicate = currentNonUnit.getPredicateLiteral(currentPosition);
		currentMatchIterator = nonUnitProver.iterator(predicate.getDescriptor(), predicate.isPositive());
	}

	private boolean setNonUnit() {
		if (nextPosition()) return true;
		return false;
	}
	
	private boolean nextPosition() {
		if (currentPosition + 1 >= currentNonUnit.getPredicateLiteralsSize()) return false;
		else currentPosition = currentPosition + 1;
		return true;
	}

}