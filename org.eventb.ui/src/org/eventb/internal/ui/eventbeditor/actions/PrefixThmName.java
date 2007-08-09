package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixThmName extends PrefixElementName {

	public void run(IAction action) {
		setPrefix("org.eventb.core.theoremLabel", "Theorem Label Prefix",
				"Please specify the prefix for theorem labels");
	}

}
