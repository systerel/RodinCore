package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixVarName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.variableIdentifier",
				"Variable Identifier Prefix",
				"Please specify the prefix for variable identifiers");
	}

}
