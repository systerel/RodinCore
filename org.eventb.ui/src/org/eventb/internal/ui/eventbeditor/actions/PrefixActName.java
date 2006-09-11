package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.internal.ui.EventBUIPlugin;

public class PrefixActName extends PrefixElementName {

	public static final QualifiedName QUALIFIED_NAME = new QualifiedName(
			EventBUIPlugin.PLUGIN_ID, "prefix-action-name");

	public static final String DEFAULT_PREFIX = "act";
	
	public void run(IAction action) {
		setPrefix(QUALIFIED_NAME, DEFAULT_PREFIX, "Action Name Prefix",
				"Please specify the prefix for action names");
	}

}
