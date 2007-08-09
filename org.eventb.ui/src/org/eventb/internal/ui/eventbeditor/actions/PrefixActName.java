package org.eventb.internal.ui.eventbeditor.actions;

public class PrefixActName extends PrefixElementName {

	public void run(org.eclipse.jface.action.IAction action) {
		setPrefix("org.eventb.core.actionLabel", "Action Label Prefix",
				"Please specify the prefix for action labels");
	}

}
