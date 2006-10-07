/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover.globaltactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IGlobalSimpleTactic;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of IGlobalExpertTactic for adding a
 *         hypothesis.
 */
public class AddHypothesis implements IGlobalSimpleTactic {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.IGlobalTactic#isEnable(org.eventb.core.prover.IProofTreeNode,
	 *      java.lang.String)
	 */
	public boolean isApplicable(IProofTreeNode node, String input) {
		return (node != null) && node.isOpen() && !input.equals("");
	}

	public ITactic getTactic(IProofTreeNode node, String input) {
		return Tactics.lemma(input);
	}
}
