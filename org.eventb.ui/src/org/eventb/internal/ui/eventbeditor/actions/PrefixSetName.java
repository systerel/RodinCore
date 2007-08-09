package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixSetName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.carrierSetIdentifier",
				"Carrier Set Identifier Prefix",
				"Please specify the prefix for carrier set identifiers");
	}

}
