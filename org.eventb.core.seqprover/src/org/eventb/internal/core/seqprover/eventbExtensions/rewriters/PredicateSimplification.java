package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Collection;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class PredicateSimplification extends
		FormulaSimplification<Predicate> {

	public PredicateSimplification(DLib lib) {
		super(lib);
	}

	protected abstract Predicate getDeterminant();

	@Override
	protected Predicate makeAssociativeFormula(int tag,
			Collection<Predicate> formulas) {
		return dLib.getFormulaFactory().makeAssociativePredicate(tag, formulas,
				null);
	}

	@Override
	protected boolean isContradicting(Predicate formula,
			Collection<Predicate> formulas) {
		final Predicate negation = dLib.makeNeg(formula);
		return formulas.contains(negation);
	}

	@Override
	protected Predicate getContradictionResult() {
		return getDeterminant();
	}
}
