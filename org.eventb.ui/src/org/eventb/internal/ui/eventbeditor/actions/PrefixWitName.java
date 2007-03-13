package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IWitness;
import org.eventb.ui.EventBUIPlugin;

public class PrefixWitName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "wit";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IWitness.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Witness Name Prefix",
				"Please specify the prefix for witness names");
	}

}
