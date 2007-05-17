package org.eventb.internal.ui.eventbeditor.actions;

import org.eventb.core.IAction;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixActName extends PrefixElementName<IAction> {

	public static final String DEFAULT_PREFIX = "act";

	public void run(org.eclipse.jface.action.IAction action) {
		setPrefix(IAction.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID,
				"Action Label Prefix",
				"Please specify the prefix for action labels");
	}

}
