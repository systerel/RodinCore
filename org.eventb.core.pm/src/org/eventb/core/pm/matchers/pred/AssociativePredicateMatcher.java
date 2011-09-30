package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;
import org.eventb.core.pm.assoc.ACPredicateProblem;
import org.eventb.core.pm.assoc.ACProblem;

/**
 * TODO better matching
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class AssociativePredicateMatcher extends PredicateMatcher<AssociativePredicate> {


	public AssociativePredicateMatcher(){
		super(AssociativePredicate.class);
	}
	
	@Override
	protected boolean gatherBindings(AssociativePredicate form,
			AssociativePredicate pattern, IBinding existingBinding){
		// get the children
		Predicate[] formChildren = form.getChildren();
		Predicate[] patternChildren = pattern.getChildren();
		ACProblem<Predicate> problem = new ACPredicateProblem(form.getTag(), formChildren, patternChildren, existingBinding);
		boolean partialMatchAcceptable = existingBinding.isPartialMatchAcceptable();
		IBinding solution = problem.solve(partialMatchAcceptable);
		if(solution != null){
			solution.makeImmutable();
			if(existingBinding.insertBinding(solution)){
				if(partialMatchAcceptable){
					existingBinding.setAssociativePredicateComplement(solution.getAssociativePredicateComplement());
				}
			}
			return true;
		}
		return false;
		
	}

	@Override
	protected AssociativePredicate getPredicate(Predicate p) {
		return (AssociativePredicate) p;
	}

}
