package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixEvtName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "evt";

	public void run(IAction action) {
		setPrefix("org.eventb.core.eventLabel",
				"Event Label Prefix",
				"Please specify the prefix for event labels");
	}

}
