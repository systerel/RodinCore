package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;

/**
 * This class is responsible for applying the resolution rule between one
 * unit and one non-unit clause. It is initialized with a new pair of clauses
 * and then, each call of {@link #nextMatch(ClauseDispatcher)} returns a new inferred clause,
 * until no more matches are available.
 *
 * @author François Terrier
 *
 */
public class ResolutionResolver implements IResolver {

	private ResolutionInferrer inferrer;
	private IMatchIterator matchedClauses;
	
	public ResolutionResolver(ResolutionInferrer inferrer, IMatchIterator matchedClauses) {
		this.inferrer = inferrer;
		this.matchedClauses = matchedClauses;
	}
	
	private Clause currentMatcher;
	private Clause currentMatched;
	private Iterator<Clause> currentMatchedIterator;
	private int currentPosition;

	public InferrenceResult next(boolean force) {
		if (!isInitialized()) throw new IllegalStateException();
		
//		if (isBlocked()) return new InferrenceResult(true); 
		
		if (nextPosition()) return doMatch();
		while (nextMatchedClause()) {
			
//			if (isBlocked() && !force) return InferrenceResult.BLOCKED_RESULT; 
			
			if (nextPosition()) {
				return doMatch();
			}
		}
		return null;
	}
	
//	private boolean isBlocked() {
//		if (currentMatched!=null && currentMatched.checkIsBlockedOnInstantiationsAndUnblock())
//			return true;
//		return false;
//		
//	}
	
	public boolean isInitialized() {
		return currentMatchedIterator != null;
	}
	
	public void initialize(Clause matcher) {
		assert matcher.isUnit();
		
		currentMatcher = matcher;
		currentMatched = null;
		currentPosition = -1;
		initMatchedIterator();
	}
	
	private void initMatchedIterator() {
		PredicateLiteral predicate = currentMatcher.getPredicateLiterals().get(0);
		currentMatchedIterator = matchedClauses.iterator(predicate.getDescriptor());
	}
	
	public void remove(Clause clause) {
		if (currentMatched != null && clause.equalsWithLevel(currentMatched)) {
			currentMatched = null;
			currentPosition = -1;
		}
		if (currentMatcher != null && clause.equalsWithLevel(currentMatcher)) {
			currentMatchedIterator = null;
			currentMatched = null;
			currentMatcher = null;
			currentPosition = -1;
		}
	}
	
	private InferrenceResult doMatch() {
		inferrer.setPosition(currentPosition);
		inferrer.setUnitClause(currentMatcher);
		currentMatched.infer(inferrer);
		InferrenceResult result = inferrer.getResult();
		if (PredicateProver.DEBUG) PredicateProver.debug("Inferred clause: "+currentMatcher+" + "+currentMatched+" -> "+result.getClause());
		return result;
	}
	
	private boolean nextMatchedClause() {
		if (!currentMatchedIterator.hasNext()) return false;
		currentMatched = currentMatchedIterator.next();
		currentPosition = -1;
		return true;
	}
	
	private boolean nextPosition() {
		if (currentMatched == null) return false;
		for (int i = currentPosition+1; i < currentMatched.getPredicateLiterals().size(); i++) {
			PredicateLiteral matcherPredicate = currentMatcher.getPredicateLiterals().get(0);
			if (currentMatched.matchesAtPosition(matcherPredicate.getDescriptor(), i)) {
				currentPosition = i;
				return true;
			}
		}
		return false;
	}
	
}
