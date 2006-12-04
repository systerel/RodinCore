package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjToImplRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegationRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TrivialRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;

/**
 * Common implementation for rewriting reasoners that rewrite a simple predicate
 * to another simple predicate.
 * 
 * @author Laurent Voisin
 */
public abstract class SimpleRewriter extends AbstractRewriter {
	
	public static class RemoveNegation extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".removeNegation";
		private static final Rewriter REWRITER = new RemoveNegationRewriter();
		public RemoveNegation() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	public static class DisjToImpl extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".disjToImpl";
		private static final Rewriter REWRITER = new DisjToImplRewriter();
		public DisjToImpl() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	public static class Trivial extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".trivial";
		private static final Rewriter REWRITER = new TrivialRewriter();
		public Trivial() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	public static class TypePred extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".trivial";
		private static final Rewriter REWRITER = new TypePredRewriter();
		public TypePred() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	private final Rewriter rewriter;
	
	public SimpleRewriter(Rewriter rewriter) {
		this.rewriter = rewriter;
	}
	
	@Override
	protected String getDisplayName(Predicate pred) {
		if (pred == null) {
			return "rewrite " + rewriter.getName() + " in goal";
		}
		return "rewrite " + rewriter.getName() + " in hyp(" + pred + ")";
	}

	@Override
	protected Action getHypAction(Predicate pred) {
		return ProverLib.deselect(new Hypothesis(pred));
	}

	@Override
	public boolean isApplicable(Predicate pred) {
		return rewriter.isApplicable(pred);
	}

	@Override
	protected Predicate[] rewrite(Predicate pred) {
		Predicate newPred = rewriter.apply(pred);
		if (newPred == null) {
			return null;
		}
		return new Predicate[] { newPred };
	}

}
