package org.eventb.internal.ui.obligationexplorer;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class ObligationExplorerUtils {

	// Debug flag
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ObligationExplorer *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
