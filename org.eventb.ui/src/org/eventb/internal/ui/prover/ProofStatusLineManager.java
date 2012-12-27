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
package org.eventb.internal.ui.prover;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eventb.core.pm.IUserSupportInformation;

public class ProofStatusLineManager {

	IActionBars actionBars;
	
	public ProofStatusLineManager (IActionBars actionBars) {
		this.actionBars = actionBars;
	}
	
	/**
	 * Set the information (in the bottom of the page).
	 * <p>
	 * 
	 * @param information
	 *            information from the UserSupport
	 */
	private void setInformation(final IUserSupportInformation information) {
		final IStatusLineManager slManager = actionBars.getStatusLineManager();
		Display display = Display.getCurrent();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				if (information == null) {
					slManager.setErrorMessage(null);
					slManager.setMessage(null);
					return;
				}
				final String message = information.getInformation().toString();
				final int priority = information.getPriority();
				if(priority == IUserSupportInformation.ERROR_PRIORITY) {
					slManager.setErrorMessage(message);
				} else {
					slManager.setErrorMessage(null);
					slManager.setMessage(message);
				}
			}

		});

		return;
	}
	
	public void setProofInformation(final IUserSupportInformation[] information) {
		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("********** MESSAGE *********");
			for (IUserSupportInformation info : information) {
				ProverUIUtils.debug(info.toString());
			}
			ProverUIUtils.debug("****************************");
		}

		int size = information.length;
		if (size == 0) {
			setInformation(null);
			return;
		}

		// Trying to print the latest message with highest priority.
		for (int priority = IUserSupportInformation.ERROR_PRIORITY; IUserSupportInformation.MIN_PRIORITY <= priority; --priority) {
			for (int i = information.length - 1; 0 <= i; --i) {
				if (information[i].getPriority() == priority) {
					setInformation(information[i]);
					return;
				}
			}
		}
		setInformation(null);
	}

}
