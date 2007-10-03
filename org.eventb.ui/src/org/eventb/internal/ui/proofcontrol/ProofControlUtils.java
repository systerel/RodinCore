package org.eventb.internal.ui.proofcontrol;

public class ProofControlUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProofControl *** ";

	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

}
