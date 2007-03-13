package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IInvariant;
import org.eventb.ui.EventBUIPlugin;

public class PrefixInvName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "inv";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IInvariant.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Invariant Name Prefix",
				"Please specify the prefix for invariant names");
	}

}
