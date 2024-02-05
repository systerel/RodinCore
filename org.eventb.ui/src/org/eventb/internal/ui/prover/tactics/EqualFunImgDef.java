/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.equalFunImgDef;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "functional image equality definition" tactics.
 *
 * This is slightly more complex than usual tactic providers because we provide
 * two tactics for a single reasoner and use a different position for the UI and
 * the core reasoner.
 *
 * In the core reasoner, the position used is the one of the functional image.
 * For example, for "f(x) = k", the position to use is 0.
 *
 * However, in the UI, we want to invoke the tactic by clicking on the equal
 * sign rather than the opening parenthesis. This can be done by using the ROOT
 * position in the UI while calling the reasoner with the position of the
 * correct child (0 or 1).
 *
 * Difficulties arise if we have a predicate like "f(x) = g(x)". For the core
 * reasoner, there are two applicable positions: 0 and 1. On the other hand, in
 * the UI, both applications are at the ROOT position. To distinguish them, we
 * define two tactics: one that applies the core reasoner on the left-hand-side
 * of the equality and one on the right-hand-side.
 *
 * @author Guillaume Verdier
 */
public class EqualFunImgDef {

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static abstract class AbstractApplication extends DefaultPositionApplication {

		public AbstractApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		protected abstract IPosition getCorePosition();

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return equalFunImgDef(hyp, getCorePosition());
		}

		@Override
		public Point getHyperlinkBounds(String parsedString, Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate, parsedString);
		}

	}

	public static class LeftApplication extends AbstractApplication {

		private static final String TACTIC_ID = "org.eventb.ui.equalFunImgDefLeft";

		public LeftApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		protected IPosition getCorePosition() {
			return position.getFirstChild();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class RightApplication extends AbstractApplication {

		private static final String TACTIC_ID = "org.eventb.ui.equalFunImgDefRight";

		public RightApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		protected IPosition getCorePosition() {
			return position.getChildAtIndex(1);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	private static abstract class AbstractTacticProvider extends AbstractHypGoalTacticProvider {

		protected abstract Expression getEqualChild(RelationalPredicate equal);

		protected abstract ITacticApplication newApplication(Predicate hyp, IPosition position);

		@Override
		protected List<ITacticApplication> getApplicationsOnPredicate(IProofTreeNode node, Predicate hyp,
				String globalInput, Predicate predicate) {
			if (predicate.getTag() == EQUAL) {
				var equal = (RelationalPredicate) predicate;
				if (getEqualChild(equal).getTag() == FUNIMAGE) {
					return singletonList(newApplication(hyp, IPosition.ROOT));
				}
			}
			return NO_APPLICATIONS;
		}

	}

	public static class Left extends AbstractTacticProvider {

		@Override
		protected Expression getEqualChild(RelationalPredicate equal) {
			return equal.getLeft();
		}

		@Override
		protected ITacticApplication newApplication(Predicate hyp, IPosition position) {
			return new LeftApplication(hyp, position);
		}

	}

	public static class Right extends AbstractTacticProvider {

		@Override
		protected Expression getEqualChild(RelationalPredicate equal) {
			return equal.getRight();
		}

		@Override
		protected ITacticApplication newApplication(Predicate hyp, IPosition position) {
			return new RightApplication(hyp, position);
		}

	}

}
