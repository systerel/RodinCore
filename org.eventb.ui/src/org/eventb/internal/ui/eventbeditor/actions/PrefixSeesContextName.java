package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.ui.EventBUIPlugin;

public class PrefixSeesContextName extends PrefixElementName {

	public static final QualifiedName QUALIFIED_NAME = new QualifiedName(
			EventBUIPlugin.PLUGIN_ID, "prefix-sees-context-name");

	public static final String DEFAULT_PREFIX = "sees";
	
	public void run(IAction action) {
		setPrefix(QUALIFIED_NAME, DEFAULT_PREFIX, "Sees Context Name Prefix",
				"Please specify the prefix for sees context names");
	}

}
