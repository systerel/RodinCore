package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eventb.core.IAction;
import org.eventb.ui.EventBUIPlugin;

public class PrefixActName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "act";

	public void run(org.eclipse.jface.action.IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IAction.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Action Name Prefix",
				"Please specify the prefix for action names");
	}

}
