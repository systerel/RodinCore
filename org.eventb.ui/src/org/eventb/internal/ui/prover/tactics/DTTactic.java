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
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;
import org.eventb.ui.prover.TacticProviderUtils;

/**
 * Abstract class for tactics about datatypes.
 * 
 * @author Nicolas Beauger
 * 
 */
public abstract class DTTactic implements ITacticProvider {

	private static final List<ITacticApplication> EMPTY_LIST = Collections
			.emptyList();

	protected static abstract class DTApplication implements
			IPositionApplication {

		private final String tactidId;
		protected final Predicate hyp;
		protected final IPosition position;

		public DTApplication(String tactidId, Predicate hyp, IPosition position) {
			this.tactidId = tactidId;
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
			return tactidId;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final Predicate goal = node.getSequent().goal();
		final Predicate pred = hyp == null ? goal : hyp;
		final List<IPosition> positions = getPositions(pred);
		if (positions.isEmpty()) {
			return EMPTY_LIST;
		}
		final List<ITacticApplication> tactics = new ArrayList<ITacticApplication>(
				positions.size());
		for (IPosition p : positions) {
			tactics.add(makeApplication(hyp, p));
		}
		return tactics;
	}

	protected abstract List<IPosition> getPositions(Predicate pred);

	protected abstract ITacticApplication makeApplication(Predicate hyp,
			IPosition p);

}