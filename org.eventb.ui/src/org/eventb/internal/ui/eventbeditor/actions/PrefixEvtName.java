package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixEvtName extends PrefixElementName<IEvent> {

	public static final String DEFAULT_PREFIX = "evt";

	public void run(IAction action) {
		setPrefix(IEvent.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID,
				"Event Label Prefix",
				"Please specify the prefix for event labels");
	}

}
