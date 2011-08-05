/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Prune implements IProofCommand {

	@Override
	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.applyTactic(Tactics.prune(), false, monitor);
	}

	@Override
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null)
			return false;
		final IProofTreeNode node = currentPO.getCurrentNode();
		return (node != null) && !node.isOpen();
	}

}
