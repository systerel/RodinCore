package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IExtendsContext;
import org.eventb.ui.EventBUIPlugin;

public class PrefixExtendsContextName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "extends";
	
	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
						EventBUIPlugin.PLUGIN_ID, IExtendsContext.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Extends Context Name Prefix",
				"Please specify the prefix for extends context names");
	}

}
