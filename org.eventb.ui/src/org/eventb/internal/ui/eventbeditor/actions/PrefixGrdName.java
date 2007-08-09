package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixGrdName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.guardLabel",
				"Guard Label Prefix",
				"Please specify the prefix for guard labels");
	}

}
