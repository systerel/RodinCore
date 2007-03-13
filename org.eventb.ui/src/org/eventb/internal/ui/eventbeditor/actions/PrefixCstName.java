package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IConstant;
import org.eventb.ui.EventBUIPlugin;

public class PrefixCstName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "cst";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IConstant.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Constant Name Prefix",
				"Please specify the prefix for constant names");
	}

}
