package org.eventb.core.seqprover.arith;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

public class ArithRewrites extends AbstractAutoRewrites implements IReasoner {

	private static final IFormulaRewriter rewriter = new ArithRewriterImpl();

	public ArithRewrites() {
		super(rewriter, true);
	}

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".arithRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "arithmatic simplification rewrites";
	}

}
