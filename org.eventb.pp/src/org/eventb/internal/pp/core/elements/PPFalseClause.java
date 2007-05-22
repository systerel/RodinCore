package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class PPFalseClause extends AbstractPPClause {

	public PPFalseClause(IOrigin origin) {
		super(origin, new ArrayList<IPredicate>(), new ArrayList<IEquality>(), new ArrayList<IArithmetic>());
	}

	@Override
	protected void computeBitSets() {
		// nothing
	}

	public void infer(IInferrer inferrer) {
		// nothing
	}

	public IClause simplify(ISimplifier simplifier) {
		return this;
	}

	public boolean isFalse() {
		return true;
	}

	public boolean isTrue() {
		return false;
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	public boolean isEquivalence() {
		return false;
	}
}
