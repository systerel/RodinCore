package org.eventb.internal.ui;

import org.eventb.core.IAssignmentElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class AssignmentLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IAssignmentElement) {
			return ((IAssignmentElement) obj).getAssignmentString();
		}
		return null;
	}

}
