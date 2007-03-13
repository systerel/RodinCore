package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IRefinesMachine;
import org.eventb.ui.EventBUIPlugin;

public class PrefixRefinesMachineName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "refines";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IRefinesMachine.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Refines Machine Name Prefix",
				"Please specify the prefix for refines machine names");
	}

}
