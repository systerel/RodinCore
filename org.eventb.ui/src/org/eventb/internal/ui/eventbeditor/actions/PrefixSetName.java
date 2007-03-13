package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.ui.EventBUIPlugin;

public class PrefixSetName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "set";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, ICarrierSet.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Carrier Set Name Prefix",
				"Please specify the prefix for carrier set names");
	}

}
