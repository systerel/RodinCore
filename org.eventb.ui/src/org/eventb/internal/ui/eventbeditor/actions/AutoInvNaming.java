package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class AutoInvNaming extends AutoElementNaming<IInvariant> {

	public void run(IAction action) {
		run(IInvariant.ELEMENT_TYPE, EditSectionRegistry.LABEL_ATTRIBUTE_ID);
	}

}
