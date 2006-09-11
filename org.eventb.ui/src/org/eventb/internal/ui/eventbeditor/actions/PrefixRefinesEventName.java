package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.internal.ui.EventBUIPlugin;

public class PrefixRefinesEventName extends PrefixElementName {

	public static final QualifiedName QUALIFIED_NAME = new QualifiedName(
			EventBUIPlugin.PLUGIN_ID, "prefix-refines-event-name");

	public static final String DEFAULT_PREFIX = "refines";
	
	public void run(IAction action) {
		setPrefix(QUALIFIED_NAME, DEFAULT_PREFIX, "Refines Event Name Prefix",
				"Please specify the prefix for refines event names");
	}

}
