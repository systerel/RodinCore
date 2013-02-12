/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added broken input repair mechanism
 *     Systerel - checked applicability to given predicate
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IRepairableInputReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.ForwardInfHypAction;

public class ContImplHypRewrites extends AbstractManualRewrites implements IRepairableInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".doubleImplGoalRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		assert pred != null;
		return "mp impl (" + pred.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula.getTag() != Formula.LIMP) {
			return null;
		}
		final BinaryPredicate predicate = (BinaryPredicate) subFormula;
		final IFormulaRewriter rewriter = new ContImplRewriter(true);
		final Predicate newSubPredicate = rewriter.rewrite(predicate);
		return pred.rewriteSubFormula(position, newSubPredicate);
	}

	@Override
	public IReasonerInput repair(IReasonerInputReader reader) {

		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) return null;

		final ForwardInfHypAction fwd = getFwd(antecedents[0]);
		if(fwd == null) return null;
		
		final Predicate hyp = getUniqueElement(fwd.getHyps());
		final Predicate infHyp = getUniqueElement(fwd.getInferredHyps());
		if (hyp == null || infHyp == null) return null;
		
		final IPosition position = findContraPosition(hyp, infHyp);
		if (position == null)
			return null;
		
		return new Input(hyp, position);
	}

	private static Predicate getUniqueElement(Collection<Predicate> preds) {
		if (preds.size() != 1) {
			return null;
		}
		return preds.iterator().next();

	}

	private static ForwardInfHypAction getFwd(IAntecedent antecedent) {
		for (IHypAction hypAction : antecedent.getHypActions()) {
			if (hypAction instanceof ForwardInfHypAction) {
				return (ForwardInfHypAction) hypAction;
			}
		}
		return null;
	}

	private IPosition findContraPosition(Predicate hyp, Predicate infHyp) {
		final List<IPosition> positions = hyp.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryPredicate predicate) {
				return Lib.isImp(predicate);
			}
		});

		IPosition contraPos = null;
		for (IPosition pos : positions) {
			final Predicate rewritten = rewrite(hyp, pos);
			if (rewritten.equals(infHyp)) {
				contraPos = pos;
				break;
			}
		}
		
		return contraPos;
	}

}
