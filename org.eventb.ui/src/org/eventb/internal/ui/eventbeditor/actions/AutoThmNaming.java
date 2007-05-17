package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.ITheorem;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class AutoThmNaming extends AutoElementNaming<ITheorem> {

	public void run(IAction action) {
		run(ITheorem.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID);
	}

}