package org.eventb.internal.ui.proofinformation;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class ProofInformationUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProofInformation *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
