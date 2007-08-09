package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixInvName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "inv";

	public void run(IAction action) {
		setPrefix("org.eventb.core.invariantLabel", "Invariant Label Prefix",
				"Please specify the prefix for invariant labels");
	}

}
