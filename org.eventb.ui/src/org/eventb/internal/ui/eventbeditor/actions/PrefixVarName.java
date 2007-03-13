package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IVariable;
import org.eventb.ui.EventBUIPlugin;

public class PrefixVarName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "var";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IVariable.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Variable Name Prefix",
				"Please specify the prefix for variable names");
	}

}
