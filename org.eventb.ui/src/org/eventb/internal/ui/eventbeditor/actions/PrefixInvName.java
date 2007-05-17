package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixInvName extends PrefixElementName<IInvariant> {

	public static final String DEFAULT_PREFIX = "inv";

	public void run(IAction action) {
		setPrefix(IInvariant.ELEMENT_TYPE,
				EditSectionRegistry.LABEL_ATTRIBUTE_ID,
				"Invariant Label Prefix",
				"Please specify the prefix for invariant labels");
	}

}
