package org.eventb.internal.ui.prooftreeui;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class ProofTreeUIUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProofTreeUI *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
