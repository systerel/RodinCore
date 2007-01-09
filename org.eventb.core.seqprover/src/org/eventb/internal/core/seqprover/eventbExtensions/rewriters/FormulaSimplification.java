package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

public class FormulaSimplification {

	public static Predicate simplifiedAssociativePredicate(AssociativePredicate predicate, Predicate[] children, Predicate neutral,
			Predicate determinant) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		boolean rewrite = false;
		for (Predicate subPred : children) {
			if (subPred.equals(determinant))
				return determinant;
			if (subPred.equals(neutral)) {
				rewrite = true;
			} else {
				predicates.add(subPred);
			}
		}

		if (rewrite) {
			if (predicates.size() == 0) {
				return neutral;
			}
			AssociativePredicate newPred = FormulaFactory.getDefault()
					.makeAssociativePredicate(predicate.getTag(),
							predicates, null);
			return newPred;
		}
		return predicate;
	}

	public static Predicate splitQuantifiedPredicate(int tag, int subTag, BoundIdentDecl[] boundIdentifiers, Predicate[] children) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Predicate child : children) {
			Predicate qPred = FormulaFactory.getDefault().makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
			predicates.add(qPred);
		}
		
		return FormulaFactory.getDefault().makeAssociativePredicate(subTag, predicates, null);
	}
}
