package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class PPTrueClause extends AbstractPPClause {

	public PPTrueClause(IOrigin origin) {
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
		return false;
	}

	public boolean isTrue() {
		return true;
	}

	@Override
	public String toString() {
		return "TRUE";
	}
}
