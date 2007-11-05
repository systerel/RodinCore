package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.SequentProver;

public class TypeRewrites extends AbstractAutoRewrites {
	
	private static final IFormulaRewriter rewriter = new TypeRewriterImpl();
	
	public TypeRewrites() {
		super(rewriter, false);
	}

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".typeRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "type rewrites";
	}

}
