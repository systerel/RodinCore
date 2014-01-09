/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public abstract class PredicatePositionReasoner implements IReasoner {

	private final static String POSITION_KEY = "pos";
	
	/**
	 * Returns the name to display in the generated rule.
	 * 
	 * @return the name to display in the rule
	 */
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null) {
			return getDisplayName() + " in " + pred.getSubFormula(position);
		}
		else {
			return getDisplayName() + " in goal";
		}
	}
	
	protected abstract String getDisplayName();

	public static class Input implements IReasonerInput, ITranslatableReasonerInput {

		private Predicate pred;

		private final IPosition position;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position) {
			this.pred = pred;
			this.position = position;
		}

		public void applyHints(ReplayHints renaming) {
			if (pred != null)
				pred = renaming.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public Predicate getPred() {
			return pred;
		}

		public IPosition getPosition() {
			return position;
		}

		@Override
		public Input translate(FormulaFactory factory) {
			if (pred == null) {
				return this;
			}
			return new Input(pred.translate(factory), position);
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			if (pred != null) {
				typeEnv.addAll(pred.getFreeIdentifiers());
			}
			return typeEnv;
		}

	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		Set<Predicate> neededHyps = reader.getNeededHyps();
		String image = reader.getString(POSITION_KEY);
		IPosition position = FormulaFactory.makePosition(image);

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new Input(null, position);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new Input(pred, position);
	}

}
