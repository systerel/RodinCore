package org.eventb.core.prover.externalReasoners.rewriter;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

public interface Rewriter {
	public boolean isApplicable(Predicate p);
	public Predicate apply(ITypeEnvironment te, Predicate p);
}
