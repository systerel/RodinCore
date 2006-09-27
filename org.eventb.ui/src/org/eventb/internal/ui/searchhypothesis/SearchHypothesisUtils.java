package org.eventb.internal.ui.searchhypothesis;

import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;

public class SearchHypothesisUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** SearchHypothesis *** ";

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

}
