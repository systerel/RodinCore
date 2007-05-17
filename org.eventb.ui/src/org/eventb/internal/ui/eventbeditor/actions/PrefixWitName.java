package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixWitName extends PrefixElementName<IWitness> {

	public static final String DEFAULT_PREFIX = "wit";

	public void run(IAction action) {
		setPrefix(IWitness.ELEMENT_TYPE,
				EditSectionRegistry.LABEL_ATTRIBUTE_ID, "Witness Name Prefix",
				"Please specify the prefix for witness names");
	}

}
