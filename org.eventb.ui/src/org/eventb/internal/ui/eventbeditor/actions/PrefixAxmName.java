package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IAxiom;
import org.eventb.ui.EventBUIPlugin;

public class PrefixAxmName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "axm";
	
	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
						EventBUIPlugin.PLUGIN_ID, IAxiom.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Axiom Name Prefix",
				"Please specify the prefix for axiom names");
	}

}
