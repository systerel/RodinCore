package org.eventb.core.prover.externalReasoners.rewriter;

import static org.eventb.core.prover.Lib.False;
import static org.eventb.core.prover.Lib.True;
import static org.eventb.core.prover.Lib.eqLeft;
import static org.eventb.core.prover.Lib.eqRight;
import static org.eventb.core.prover.Lib.getSet;
import static org.eventb.core.prover.Lib.isEmptySet;
import static org.eventb.core.prover.Lib.isEq;
import static org.eventb.core.prover.Lib.isInclusion;
import static org.eventb.core.prover.Lib.isNotEq;
import static org.eventb.core.prover.Lib.isNotInclusion;
import static org.eventb.core.prover.Lib.notEqLeft;
import static org.eventb.core.prover.Lib.notEqRight;

import org.eventb.core.ast.Predicate;

public class TypeExpRewrites implements Rewriter{

	public boolean isApplicable(Predicate p) {
		if (isNotEq(p)) {
			if (isEmptySet(notEqRight(p)) &&
					notEqLeft(p).isATypeExpression())
				return true;
			if (isEmptySet(notEqLeft(p)) &&
					notEqRight(p).isATypeExpression())
				return true;
		}
		
		if (isEq(p)) {
			if (isEmptySet(eqRight(p)) &&
					eqLeft(p).isATypeExpression())
				return true;
			if (isEmptySet(eqLeft(p)) &&
					eqRight(p).isATypeExpression())
				return true;
		}
			
		if (isInclusion(p) && getSet(p).isATypeExpression())
			return true;
		
		if (isNotInclusion(p) && getSet(p).isATypeExpression())
			return true;
		
		return false;
	}

	public Predicate apply(Predicate p) {
		if (isNotEq(p)) {
			if (isEmptySet(notEqRight(p)) &&
					notEqLeft(p).isATypeExpression())
				return True;
			if (isEmptySet(notEqLeft(p)) &&
					notEqRight(p).isATypeExpression())
				return True;
		}
		
		if (isEq(p)) {
			if (isEmptySet(eqRight(p)) &&
					eqLeft(p).isATypeExpression())
				return False;
			if (isEmptySet(eqLeft(p)) &&
					eqRight(p).isATypeExpression())
				return False;
		}
			
		if (isInclusion(p) && getSet(p).isATypeExpression())
			//	&& typeToExpression(te,getElement(p).getType()).equals(getSet(p)))
			return True;
		
		if (isNotInclusion(p) && getSet(p).isATypeExpression())
			//	&& typeToExpression(te,getElement(p).getType()).equals(getSet(p)))
			return False;
		
		
		return null;
	}

}
