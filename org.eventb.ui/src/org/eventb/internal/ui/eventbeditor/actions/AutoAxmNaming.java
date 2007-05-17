package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class AutoAxmNaming extends NewAutoElementNaming<IAxiom> {

	public void run(IAction action) {
		run(IAxiom.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID);
	}


}