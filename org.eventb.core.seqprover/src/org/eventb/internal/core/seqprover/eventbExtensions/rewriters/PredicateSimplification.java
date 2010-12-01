package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class PredicateSimplification extends
		FormulaSimplification<Predicate> {

	public static class LandSimplification extends PredicateSimplification {

		public LandSimplification(AssociativePredicate original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected int neutralTag() {
			return BTRUE;
		}

		@Override
		protected int determinantTag() {
			return BFALSE;
		}

	}

	public static class LorSimplification extends PredicateSimplification {

		public LorSimplification(AssociativePredicate original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected int neutralTag() {
			return BFALSE;
		}

		@Override
		protected int determinantTag() {
			return BTRUE;
		}

	}

	public PredicateSimplification(AssociativePredicate original, DLib dLib) {
		super(original, original.getChildren(), dLib);
	}

	@Override
	protected Predicate makeAssociativeFormula() {
		return ff
				.makeAssociativePredicate(original.getTag(), newChildren, null);
	}

	@Override
	protected boolean isContradicting(Predicate child) {
		final Predicate negation = dLib.makeNeg(child);
		return newChildren.contains(negation);
	}

	@Override
	protected Predicate getContradictionResult() {
		return getDeterminant();
	}

	@Override
	protected boolean eliminateDuplicate() {
		return true;
	}

	@Override
	protected Predicate getNeutral() {
		return ff.makeLiteralPredicate(neutralTag(), null);
	}

	@Override
	protected boolean isNeutral(Predicate child) {
		return child.getTag() == neutralTag();
	}

	protected abstract int neutralTag();

	protected Predicate getDeterminant() {
		return ff.makeLiteralPredicate(determinantTag(), null);
	}

	@Override
	protected boolean isDeterminant(Predicate child) {
		return child.getTag() == determinantTag();
	}

	protected abstract int determinantTag();

}
