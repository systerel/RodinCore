package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IEvent;
import org.eventb.ui.EventBUIPlugin;

public class PrefixEvtName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "evt";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IEvent.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Event Name Prefix",
				"Please specify the prefix for event names");
	}

}
