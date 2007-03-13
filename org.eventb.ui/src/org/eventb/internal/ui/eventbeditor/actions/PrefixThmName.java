package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.ITheorem;
import org.eventb.ui.EventBUIPlugin;

public class PrefixThmName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "thm";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, ITheorem.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Theorem Name Prefix",
				"Please specify the prefix for theorem names");
	}

}
