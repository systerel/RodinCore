package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class LandSimplification extends PredicateSimplification {

	public LandSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected Predicate getDeterminant() {
		return dLib.getFormulaFactory().makeLiteralPredicate(Predicate.BFALSE,
				null);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return true;
	}

	@Override
	protected boolean isNeutral(Predicate formula) {
		return formula.getTag() == Predicate.BTRUE;
	}

	@Override
	protected boolean isDeterminant(Predicate formula) {
		return formula.getTag() == Predicate.BFALSE;
	}

	@Override
	protected Predicate getNeutral(Predicate formula) {
		return dLib.getFormulaFactory().makeLiteralPredicate(Predicate.BTRUE,
				null);
	}

}
