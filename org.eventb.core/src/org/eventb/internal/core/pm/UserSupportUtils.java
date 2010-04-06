package org.eventb.internal.core.pm;

import java.util.StringTokenizer;


public class UserSupportUtils {
	
	public static boolean DEBUG = false;
	
	private static final String DEBUG_PREFIX = "*** User Support *** ";
	
	public static void debug(String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "\n\r", true);
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if (!token.equals("\n"))
				System.out.println(DEBUG_PREFIX + token);
		}
	}
}
