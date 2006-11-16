package org.eventb.internal.core.pm;

import org.eventb.core.pm.IProvingMode;

public class ProvingMode implements IProvingMode {

	private boolean expertMode = false;

	public boolean isExpertMode() {
		return expertMode;
	}

	public void setExpertMode(boolean mode) {
		expertMode = mode;
	}

}
