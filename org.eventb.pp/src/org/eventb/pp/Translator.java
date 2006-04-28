package org.eventb.pp;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.translator.BoundIdentifierDecomposition;
import org.eventb.internal.pp.translator.FreeIdentifierDecomposition;
import org.eventb.internal.pp.translator.GoalChecker;
import org.eventb.internal.pp.translator.PredicateSimplification;

public abstract class Translator {
	
	public static Predicate decomposeIdentifiers(Predicate predicate, FormulaFactory ff) {
		predicate = FreeIdentifierDecomposition.decomposeIdentifiers(predicate, ff);
		return BoundIdentifierDecomposition.decomposeBoundIdentifiers(predicate, ff);
	}
	
	public static Predicate reduceToPredicateCalulus(Predicate predicate, FormulaFactory ff) {
		return org.eventb.internal.pp.translator.Translator.reduceToPredCalc(predicate, ff);
	}
	
	public static Predicate simplifyPredicate(Predicate predicate, FormulaFactory ff) {
		return PredicateSimplification.simplifyPredicate(predicate, ff);
	}

	public static boolean isInGoal(Predicate predicate) {
		return GoalChecker.isInGoal(predicate);
	}
}
