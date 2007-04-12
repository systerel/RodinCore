package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

public class EquivalenceClauseKey extends ClauseKey<EquivalenceClauseDescriptor> {

	public EquivalenceClauseKey(List<ISignedFormula> signatures) {
		super(signatures);
	}

	@Override
	public EquivalenceClauseDescriptor newDescriptor(IContext context) {
		return new EquivalenceClauseDescriptor(context, context.getNextLiteralIdentifier());
	}

}
