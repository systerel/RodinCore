package org.eventb.internal.eventBKeyboard;

public class KeyboardUtils {

	public static boolean TEXT_DEBUG = false;
	
	public static boolean MATH_DEBUG = false;
	
	public static void debugText(String str) {
		if (TEXT_DEBUG) System.out.println(str);
	}
	
	public static void debugMath(String str) {
		if (MATH_DEBUG) System.out.println(str);
	}
	
}
