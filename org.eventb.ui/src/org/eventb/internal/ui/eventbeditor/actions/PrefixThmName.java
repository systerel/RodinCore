package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.ITheorem;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixThmName extends PrefixElementName<ITheorem> {

	public static final String DEFAULT_PREFIX = "thm";

	public void run(IAction action) {
		setPrefix(ITheorem.ELEMENT_TYPE,
				EditSectionRegistry.LABEL_ATTRIBUTE_ID, "Theorem Label Prefix",
				"Please specify the prefix for theorem labels");
	}

}
