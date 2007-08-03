package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.SequentProver;

public class TypeRewrites extends AbstractAutoRewrites {
	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".typeRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final IFormulaRewriter rewriter = new TypeRewriterImpl();

		return apply(seq, input, rewriter, false, pm);
	}

	@Override
	protected String getDisplayName() {
		return "type rewrites";
	}

}
