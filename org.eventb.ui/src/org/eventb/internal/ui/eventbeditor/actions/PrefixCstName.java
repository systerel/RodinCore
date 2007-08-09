package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;

public class PrefixCstName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "cst";

	public void run(IAction action) {
		setPrefix("org.eventb.core.constantIdentifier", "Constant Name Prefix",
				"Please specify the prefix for constant names");
	}

}
