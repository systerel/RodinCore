/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider2;
import org.eventb.ui.prover.TacticProviderUtils;

/**
 * @author Nicolas Beauger
 *
 */
public class DTDistinctCase implements
		ITacticProvider2 {
	
	private static class DtDCApplication implements IPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.dtDistinctCase";

		private final Predicate hyp;
		private final IPosition position;

		public DtDCApplication(Predicate hyp, IPosition position) {
			this.hyp = hyp;
			this.position = position;
		}

		@Override
		public String getHyperlinkLabel() {
			return null;
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return TacticProviderUtils.getOperatorPosition(parsedPredicate,
					parsedString, position.getFirstChild());
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public ITactic getTactic(String[] inputs, String gInput) {
			return Tactics.dtDistinctCase(hyp, position);
		}
	}

	private static final List<ITacticApplication> EMPTY_LIST = Collections
			.emptyList();

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final Predicate goal = node.getSequent().goal();
		final Predicate pred = hyp == null ? goal : hyp;
		final List<IPosition> positions = Tactics.dtDistinctCaseGetPositions(pred);
		if (positions.isEmpty()) {
			return EMPTY_LIST;
		}
		final List<ITacticApplication> tactics = new ArrayList<ITacticApplication>(
				positions.size());
		for (IPosition p : positions) {
			tactics.add(new DtDCApplication(hyp, p));
		}
		return tactics;
	}


}
