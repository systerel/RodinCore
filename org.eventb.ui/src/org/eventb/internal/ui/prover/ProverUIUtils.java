package org.eventb.internal.ui.prover;

public class ProverUIUtils {

	/**
	 * Print out the message if the <code>ProverUI.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugProverUI(String message) {
		if (ProverUI.DEBUG)
			System.out.println(message);
	}
	
}
