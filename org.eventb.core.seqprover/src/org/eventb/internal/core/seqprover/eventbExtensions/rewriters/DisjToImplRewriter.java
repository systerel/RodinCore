package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.seqprover.eventbExtensions.Lib.disjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isDisj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.makeDisj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.makeImp;
import static org.eventb.core.seqprover.eventbExtensions.Lib.makeNeg;

import org.eventb.core.ast.Predicate;

/**
 * @author fmehta
 * @deprecated use {@link DisjunctionToImplicationRewriter} instead
 */
@Deprecated
public class DisjToImplRewriter implements Rewriter{
	
	public String getRewriterID() {
		return "disjToImpl";
	}
	
	public String getName() {
		return "∨ to ⇒";
	}
	
	public boolean isApplicable(Predicate p) {
		if (isDisj(p)) return true;
		
		return false;
	}

	public Predicate apply(Predicate p) {
		// (P or Q or ...) == (-P => (Q or ..))
		if (isDisj(p))
		{
			Predicate[] disjuncts = disjuncts(p);
			assert disjuncts.length >= 2;
			Predicate firstDisjunct = disjuncts[0];
			Predicate[] restDisjuncts = new Predicate[disjuncts.length - 1];
			System.arraycopy(disjuncts,1,restDisjuncts,0,disjuncts.length - 1);
			return makeImp(
					makeNeg(firstDisjunct),
					makeDisj(restDisjuncts)
					);
		}

		return null;
	}

}
