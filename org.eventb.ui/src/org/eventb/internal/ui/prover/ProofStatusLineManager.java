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
	 * @param information
	 *            the string (information from the UserSupport).
	 */
	private void setInformation(final String information) {
		final IStatusLineManager slManager = actionBars.getStatusLineManager();
		Display display = Display.getCurrent();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				slManager.setMessage(information);
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
			setInformation("");
			return;
		}

		// Trying to print the latest message with highest priority.
		for (int priority = IUserSupportInformation.MAX_PRIORITY; IUserSupportInformation.MIN_PRIORITY <= priority; --priority) {
			for (int i = information.length - 1; 0 <= i; --i) {
				if (information[i].getPriority() == priority) {
					setInformation(information[i].getInformation()
							.toString());
					return;
				}
			}
		}
		setInformation("");
	}

}
