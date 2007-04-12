package org.eventb.internal.pp.loader.formula.descriptor;

import org.eventb.internal.pp.loader.predicate.IContext;

public class ArithmeticDescriptor extends LiteralDescriptor {

	public ArithmeticDescriptor(IContext context) {
		super(context);
	}

	@Override
	public String toString() {
		return "A";
	}


}
