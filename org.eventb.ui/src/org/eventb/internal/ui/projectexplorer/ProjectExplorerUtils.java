package org.eventb.internal.ui.projectexplorer;

public class ProjectExplorerUtils {

	// Debug flag
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProjectExplorer *** ";

	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

}
