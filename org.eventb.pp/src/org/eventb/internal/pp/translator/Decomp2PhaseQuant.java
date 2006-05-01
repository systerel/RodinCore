package org.eventb.internal.pp.translator;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

public class Decomp2PhaseQuant extends DecomposedQuant {
	
	int count = 0;
	
	boolean recording = true;
	
	public Decomp2PhaseQuant(FormulaFactory ff) {
		super(ff);
	}
	
	@Override
	public Expression push(Expression expr) {
		if(recording) return expr;
		else return expr.shiftBoundIdentifiers(count, ff);
	}
	
	public void startPhase2() {
		assert recording : "Recoring was already finished";
		recording = false;
		count = offset();
		identDecls.clear();
		c.reset();	
	}
}
