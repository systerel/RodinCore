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

import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.reasoners.ExternalML;
import org.eventb.internal.ui.proofcontrol.ProofControlPage;
import org.eventb.ui.prover.IGlobalTactic;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of IGlobalTactic for applying
 *         mono-lemma external prover in force 3.
 */
public class M3 implements IGlobalTactic {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.IGlobalTactic#isEnable(org.eventb.core.prover.IProofTreeNode,
	 *      java.lang.String)
	 */
	public boolean isApplicable(IProofTreeNode node, String input) {
		return node != null && node.isOpen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.IGlobalTactic#apply(org.eventb.core.pm.UserSupport,
	 *      java.lang.String)
	 */
	public void apply(UserSupport userSupport, String input)
			throws RodinDBException {
		ProofControlPage.runML(userSupport, ExternalML.Input.FORCE_3);
	}

}
