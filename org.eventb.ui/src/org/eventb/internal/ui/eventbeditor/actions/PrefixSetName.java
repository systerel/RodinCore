package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixSetName extends PrefixElementName<ICarrierSet> {

	public static final String DEFAULT_PREFIX = "set";

	public void run(IAction action) {
		setPrefix(ICarrierSet.ELEMENT_TYPE,
				EditSectionRegistry.IDENTIFIER_ATTRIBUTE_ID,
				"Carrier Set Identifier Prefix",
				"Please specify the prefix for carrier set identifiers");
	}

}
