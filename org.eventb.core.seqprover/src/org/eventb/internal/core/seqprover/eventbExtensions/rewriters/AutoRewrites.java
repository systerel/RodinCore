package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.SequentProver;

public class AutoRewrites extends AbstractAutoRewrites {

	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();

	public AutoRewrites() {
		super(rewriter, true);
	}

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "simplification rewrites";
	}

}
