package org.eventb.core.seqprover.reasoners.rewriter;

import org.eventb.core.ast.Predicate;

public interface Rewriter {
	
	public String getRewriterID();
	public String getName();
	public boolean isApplicable(Predicate p);
	public Predicate apply(Predicate p);
	
}
