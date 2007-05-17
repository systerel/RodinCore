package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixAxmName extends PrefixElementName<IAxiom> {

	public static final String DEFAULT_PREFIX = EditSectionRegistry
			.getDefault().getDefaultPrefix(IAxiom.ELEMENT_TYPE,
					"org.eventb.ui.label");

	public void run(IAction action) {
		setPrefix(IAxiom.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID,
				"Axiom Label Prefix",
				"Please specify the prefix for axiom labels");
	}

}
