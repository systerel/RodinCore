package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.ISeesContext;
import org.eventb.ui.EventBUIPlugin;

public class PrefixSeesContextName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "sees";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, ISeesContext.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Sees Context Name Prefix",
				"Please specify the prefix for sees context names");
	}

}
