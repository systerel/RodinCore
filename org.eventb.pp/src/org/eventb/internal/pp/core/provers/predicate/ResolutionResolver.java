package org.eventb.internal.pp.core.provers.predicate;

import java.util.List;

import org.eventb.internal.pp.core.ProofStrategy;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;

/**
 * This class is responsible for applying the resolution rule between one
 * unit and one non-unit clause. It is initialized with a new pair of clauses
 * and then, each call of {@link #nextMatch(ProofStrategy)} returns a new inferred clause,
 * until no more matches are available.
 *
 * @author François Terrier
 *
 */
public class ResolutionResolver {
	// STATELESS ?
	private IClause unitClause;
	private IClause clause;
	private ResolutionInferrer inferrer;
	
	public ResolutionResolver(ResolutionInferrer inferrer) {
		this.inferrer = inferrer;
	}
	
	public void initialize(IClause unitClause, IClause clause) {
		this.unitClause = unitClause;
		this.clause = clause;
		this.currentLiteral = 0;
	}
	
	private int currentLiteral = 0;
	
	private void reset() {
		this.unitClause = null;
		this.clause = null;
		this.currentLiteral = 0;
	}
	
	public void removeClause(IClause clause) {
		if (this.clause == clause || this.unitClause == clause) reset();
	}
	
	public InferrenceResult next() {
		if (unitClause == null || clause == null) return null;
		
		List<IPredicate> literals = clause.getPredicateLiterals();

		inferrer.setUnitClause(unitClause);
		for (int i = currentLiteral;i<literals.size();i++) {
			inferrer.setPosition(i);
			if (inferrer.canInfer(clause)) {
				currentLiteral = i+1;
				clause.infer(inferrer);
				return inferrer.getResult();
			}
		}
		return null;
	}
	

}