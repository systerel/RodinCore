package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixAxmName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.axiomLabel", "Axiom Label Prefix",
				"Please specify the prefix for axiom labels");
	}

}
