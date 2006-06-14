package org.eventb.core.prover.reasoners.rewriter;

import org.eventb.core.ast.Predicate;

public interface Rewriter {
	
	public String getRewriterID();
	public boolean isApplicable(Predicate p);
	public Predicate apply(Predicate p);
}
