package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;

public class ReverseResolutionResolver implements IResolver {

	private ResolutionInferrer inferrer;
	private UnitMatchIterator nonUnitProver;
	
	public ReverseResolutionResolver(ResolutionInferrer inferrer, UnitMatchIterator nonUnitProver) {
		this.inferrer = inferrer;
		this.nonUnitProver = nonUnitProver;
	}
	
	private int currentPosition;
//	private boolean currentMatch = true;
	private Clause currentNonUnit;
	private Clause currentUnit;
	private Iterator<Clause> currentMatchIterator;
	
	public InferrenceResult next() {
		if (setUnit()) return doMatch();
		while (setNonUnit()) {
			initMatchIterator();
			if (setUnit()) {
				return doMatch();
			}
		}
		return null;
	}
	
	public void initialize(Clause nonUnitClause) {
		this.currentNonUnit = nonUnitClause;
//		currentMatch = false;
		currentPosition = -1;
		currentMatchIterator = null;
	}
	
	public boolean isInitialized() {
		return currentNonUnit != null;
	}

	private boolean setUnit() {
		if (currentMatchIterator == null) return false;
		while (currentMatchIterator.hasNext()) {
			currentUnit = currentMatchIterator.next();
			if (currentNonUnit.matchesAtPosition(currentUnit.getPredicateLiterals().get(0).getDescriptor(), currentPosition)) {
				return true;
			}
		}
		return false;
	}
	
	private InferrenceResult doMatch() {
		inferrer.setPosition(currentPosition);
		inferrer.setUnitClause(currentUnit);
		currentNonUnit.infer(inferrer);
		InferrenceResult result = inferrer.getResult();
		if (PredicateProver.DEBUG) PredicateProver.debug("Inferred clause: "+currentUnit+" + "+currentNonUnit+" -> "+result.getClause());
		return result;
	}

	private void initMatchIterator() {
		PredicateLiteral predicate = currentNonUnit.getPredicateLiterals().get(currentPosition);
		currentMatchIterator = nonUnitProver.iterator(predicate.getDescriptor());
	}

	private boolean setNonUnit() {
//		if (nextMatch()) return true;
		if (nextPosition()) return true;
		return false;
	}
	
//	private boolean nextMatch() {
//		if (currentMatch == false) return false;
//		if (!currentNonUnit.isEquivalence()) return false;
//		else currentMatch = false;
//		return true;
//	}
	
	private boolean nextPosition() {
//		currentMatch = true;
		if (currentPosition + 1 >= currentNonUnit.getPredicateLiterals().size()) return false;
		else currentPosition = currentPosition + 1;
		return true;
	}

}