/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static org.eventb.core.ast.IPosition.ROOT;

import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "db impl" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.doubleImpIHyp</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 */
public class DoubleImplHyp implements ITacticProvider {
	
	public static class DoubleImplHypApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.doubleImpIHyp";

		public DoubleImplHypApplication(Predicate hyp) {
			super(hyp, ROOT);
		}
		
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.doubleImpHyp(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}
		
	}
	
	public static class DoubleImplHypApplicationInspector extends DefaultApplicationInspector {

		public DoubleImplHypApplicationInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (isDoubleImplPredicate(predicate)) {
				accumulator.add(new DoubleImplHypApplication(hyp));
			}
		}

		public static boolean isDoubleImplPredicate(Predicate predicate) {
			if (Lib.isImp(predicate)) {
				BinaryPredicate bPred = (BinaryPredicate) predicate;
				if (Lib.isImp(bPred.getRight())) {
					return true;
				}
			}
			return false;
		}
		
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null)
			return emptyList();
		return hyp.inspect(new DoubleImplHypApplicationInspector(hyp));
	}

}
