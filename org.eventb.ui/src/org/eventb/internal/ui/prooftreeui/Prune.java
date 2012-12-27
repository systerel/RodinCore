/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Refactored defining AbstractProofTreeAction
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class Prune extends AbstractProofTreeAction {

	public Prune() {
		super(false);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		setUserSupport(targetPart);
	}

	@Override
	public void run(IAction action) {
		applyTactic(Tactics.prune(), false);
	}

	@Override
	protected boolean isEnabled(IAction action) {
		if (isInProofSkeletonView(action)) {
			traceDisabledness("In proof skeleton view", action);
			return false;
		}
		if (!isUserSupportPresent(action)) {
			traceDisabledness("No user support present", action);
			return false;
		}
		return super.isEnabled(action);
	}
	
}
