package org.eventb.internal.ui.cachehypothesis;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class CacheHypothesisUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** Cache Hypothesis *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
