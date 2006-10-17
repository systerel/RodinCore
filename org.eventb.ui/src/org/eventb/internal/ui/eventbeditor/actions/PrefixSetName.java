package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.ui.EventBUIPlugin;

public class PrefixSetName extends PrefixElementName {

	public static final QualifiedName QUALIFIED_NAME = new QualifiedName(
			EventBUIPlugin.PLUGIN_ID, "prefix-carrier-set-name");

	public static final String DEFAULT_PREFIX = "set";
	
	public void run(IAction action) {
		setPrefix(QUALIFIED_NAME, DEFAULT_PREFIX, "Carrier Set Name Prefix",
				"Please specify the prefix for carrier set names");
	}

}
