package org.eventb.internal.core.pm;


public class UserSupportUtils {
	
	public static boolean DEBUG = false;
	
	private static final String DEBUG_PREFIX = "*** User Support ***";
	
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}
}
