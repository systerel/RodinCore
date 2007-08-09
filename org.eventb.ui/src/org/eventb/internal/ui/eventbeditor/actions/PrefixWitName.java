package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixWitName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.witnessLabel", "Witness Name Prefix",
				"Please specify the prefix for witness names");
	}

}
