/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import static java.util.Collections.singletonList;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ISequentTranslator;
import org.eventb.core.seqprover.transformer.ISimpleSequent;

/**
 * In practice, hypotheses are always before the goal in <code>predicates</code>
 * , however this is not part of the published interface. It is currently used
 * only by {@link #toString()}.
 * 
 * @author Laurent Voisin
 */
public class SimpleSequent implements ISimpleSequent {

	private final ISealedTypeEnvironment typenv;
	private final TrackedPredicate[] predicates;
	private final Object origin;

	private static TrackedPredicate[] filter(List<TrackedPredicate> preds) {
		final List<TrackedPredicate> newPreds = new ArrayList<TrackedPredicate>(
				preds.size());
		for (TrackedPredicate pred : preds) {
			if (pred != null && pred.isUseful()) {
				newPreds.add(pred);
			}
		}
		return newPreds.toArray(new TrackedPredicate[newPreds.size()]);
	}

	public SimpleSequent(FormulaFactory factory, List<TrackedPredicate> preds,
			Object origin) {
		this.predicates = filter(preds);
		this.origin = origin;
		this.typenv = fillTypeEnvironment(factory);
	}

	public SimpleSequent(FormulaFactory factory, TrackedPredicate trivial,
			Object origin) {
		this(factory, singletonList(trivial), origin);
		assert trivial.holdsTrivially();
	}

	// Must be called by constructor only
	private ISealedTypeEnvironment fillTypeEnvironment(FormulaFactory factory) {
		ITypeEnvironmentBuilder typenvBuilder = factory.makeTypeEnvironment();
		for (TrackedPredicate tpred : predicates) {
			final Predicate pred = tpred.getPredicate();
			assert pred.isTypeChecked();
			// TODO move all this into AST library: pred.typeEnvironment()
			typenvBuilder.addAll(pred.getFreeIdentifiers());
		}
		return typenvBuilder.makeSnapshot();
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return typenv.getFormulaFactory();
	}

	@Override
	public ISealedTypeEnvironment getTypeEnvironment() {
		return typenv;
	}

	@Override
	public TrackedPredicate getTrivialPredicate() {
		if (predicates.length == 1) {
			final TrackedPredicate pred = predicates[0];
			if (pred.holdsTrivially()) {
				return pred;
			}
		}
		return null;
	}

	@Override
	public TrackedPredicate[] getPredicates() {
		return predicates.clone();
	}

	@Override
	public Object getOrigin() {
		return origin;
	}

	@Override
	public ISimpleSequent apply(ISequentTransformer transformer) {
		final List<TrackedPredicate> newPreds = new ArrayList<TrackedPredicate>(
				predicates.length);
		final FormulaFactory targetFac = getTargetFormulaFactory(transformer);
		boolean changed = targetFac != getFormulaFactory();
		for (TrackedPredicate pred : predicates) {
			final TrackedPredicate newPred = pred.transform(transformer);
			changed |= newPred != pred;
			if (newPred == null) {
				continue;
			}
			if (newPred.holdsTrivially()) {
				return new SimpleSequent(targetFac, newPred, origin);
			}
			newPreds.add(newPred);
		}
		if (!changed) {
			return this;
		}
		prependAxioms(newPreds, transformer);
		return new SimpleSequent(targetFac, newPreds, origin);
	}

	private FormulaFactory getTargetFormulaFactory(
			ISequentTransformer transformer) {
		if (transformer instanceof ISequentTranslator) {
			return ((ISequentTranslator) transformer).getTargetFormulaFactory();
		}
		return getFormulaFactory();
	}

	private void prependAxioms(List<TrackedPredicate> newPreds,
			ISequentTransformer transformer) {
		if (transformer instanceof ISequentTranslator) {
			final ISequentTranslator translator = (ISequentTranslator) transformer;
			newPreds.addAll(0, makeTrackedPredicates(translator.getAxioms()));
		}
	}

	private static List<TrackedPredicate> makeTrackedPredicates(
			Predicate[] hypotheses) {
		final List<TrackedPredicate> preds = new ArrayList<TrackedPredicate>();
		for (final Predicate hyp : hypotheses) {
			preds.add(makeHyp(hyp));
		}
		return preds;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		for (TrackedPredicate tpred : predicates) {
			if (tpred.isHypothesis()) {
				sb.append(sep);
				sep = " ;; ";
			} else {
				if (sep.length() != 0) {
					sb.append(' ');
				}
				sb.append("|- ");
			}
			sb.append(tpred.getPredicate());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + typenv.hashCode();
		result = prime * result + Arrays.hashCode(predicates);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final SimpleSequent other = (SimpleSequent) obj;
		return this.typenv.equals(other.typenv)
				&& Arrays.equals(this.predicates, other.predicates);
	}

}
