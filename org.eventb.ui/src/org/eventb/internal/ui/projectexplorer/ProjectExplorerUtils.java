package org.eventb.internal.ui.projectexplorer;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class ProjectExplorerUtils {

	// Debug flag
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProjectExplorer *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
