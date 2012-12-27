/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class NextReview implements IProofCommand {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	@Override
	public void apply(final IUserSupport us, Predicate hyp, String[] inputs,
			final IProgressMonitor monitor) throws RodinDBException {
		USM.run(new Runnable() {

			@Override
			public void run() {
				boolean b = us.selectNextSubgoal(false, new IProofTreeNodeFilter() {

					@Override
					public boolean select(IProofTreeNode node) {
						int confidence = node.getConfidence();
						return !node.isOpen() && !node.hasChildren()
								&& confidence <= IConfidence.REVIEWED_MAX
								&& IConfidence.PENDING <= confidence;
					}
					
				});
				if (b) 
					us.applyTactic(Tactics.prune(), false, monitor);
			}
			
		});
		
	}

	@Override
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		IProofState currentPO = us.getCurrentPO();
		return (currentPO != null);
	}

}
