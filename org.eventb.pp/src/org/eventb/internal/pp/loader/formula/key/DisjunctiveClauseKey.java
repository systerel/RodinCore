package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

public class DisjunctiveClauseKey extends ClauseKey<DisjunctiveClauseDescriptor> {

	public DisjunctiveClauseKey(List<ISignedFormula> signatures) {
		super(signatures);
	}

	@Override
	public DisjunctiveClauseDescriptor newDescriptor(IContext context) {
		return new DisjunctiveClauseDescriptor(context, context.getNextLiteralIdentifier());
	}

}
