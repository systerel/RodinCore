package org.eventb.internal.ui.proofcontrol;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class ProofControlUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProofControl *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
