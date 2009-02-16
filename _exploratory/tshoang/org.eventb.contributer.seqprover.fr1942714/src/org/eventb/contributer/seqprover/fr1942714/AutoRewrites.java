package org.eventb.contributer.seqprover.fr1942714;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

@SuppressWarnings("restriction")
public class AutoRewrites extends AbstractAutoRewrites implements IReasoner {
		 
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();

	public AutoRewrites() {
		super(rewriter, true);
	}

	public static String REASONER_ID = "autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "AutomaticRewrites";
	}

		 
}