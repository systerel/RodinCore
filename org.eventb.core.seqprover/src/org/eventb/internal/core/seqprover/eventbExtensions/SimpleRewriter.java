/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjToImplRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegationRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TrivialRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;

/**
 * Common implementation for rewriting reasoners that rewrite a simple predicate
 * to another simple predicate.
 * 
 * @deprecated
 * 			All SimpleRewrites in this class are deprecated since their functionality is redundant.
 * 			Use {@link AutoRewrites} instead
 * 			
 * 
 * @author Laurent Voisin
 */
public abstract class SimpleRewriter extends AbstractRewriter {
	
	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
	public static class TypePred extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".typePred";
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
	protected IHypAction getHypAction(Predicate pred) {
		return ProverFactory.makeDeselectHypAction(Arrays.asList(pred));
	}

	@Override
	public boolean isApplicable(Predicate pred) {
		return rewriter.isApplicable(pred);
	}

	@Override
	protected Predicate[] rewrite(Predicate pred, FormulaFactory ff) {
		Predicate newPred = rewriter.apply(pred, ff);
		if (newPred == null) {
			return null;
		}
		return new Predicate[] { newPred };
	}

}
