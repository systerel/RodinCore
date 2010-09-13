/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "∨ to ⇒ in" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.disjToImpl</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class DisjToImpl extends AbstractHypGoalTacticProvider {

	public static class DisjToImplApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.disjToImpl";

		public DisjToImplApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.disjToImpl(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}
		
		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate,	parsedString);
		}
		
		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			final AssociativePredicate subFormula = (AssociativePredicate) predicate
			.getSubFormula(position);
			final Predicate[] children = subFormula.getChildren();
			final Predicate first = children[0];
			final Predicate second = children[1];
			// Return the operator between the first and second child
			return getOperatorPosition(predStr,
					first.getSourceLocation().getEnd() + 1, second
					.getSourceLocation().getStart());
		}
	}
	
	public static class DisjToImplAppliInspector extends DefaultApplicationInspector {

		public DisjToImplAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.LOR) {
				accumulator.add(new DisjToImplApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}
		
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new DisjToImplAppliInspector(hyp));
	}
	
}
