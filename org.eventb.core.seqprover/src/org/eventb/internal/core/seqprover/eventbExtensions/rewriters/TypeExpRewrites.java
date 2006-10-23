package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.seqprover.eventbExtensions.Lib.False;
import static org.eventb.core.seqprover.eventbExtensions.Lib.True;
import static org.eventb.core.seqprover.eventbExtensions.Lib.eqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.eqRight;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getSet;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isEmptySet;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqRight;

import org.eventb.core.ast.Predicate;

public class TypeExpRewrites implements Rewriter{

	public String getRewriterID() {
		return "typeExpRewrites";
	}
	
	public String getName() {
		return "type properties in";
	}
	
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
