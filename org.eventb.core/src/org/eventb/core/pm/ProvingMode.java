package org.eventb.core.pm;

public class ProvingMode {

	// TODO Set expertMode from the Preferences?
	private static boolean expertMode = false;

	public static boolean isExpertMode() {
		return expertMode;
	}

	public static void setExpertMode(boolean mode) {
		expertMode = mode;
	}

}
