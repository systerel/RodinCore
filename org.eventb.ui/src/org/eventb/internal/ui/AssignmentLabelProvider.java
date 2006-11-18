package org.eventb.internal.ui;

import org.eventb.core.IAssignmentElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class AssignmentLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IAssignmentElement) {
			try {
				return ((IAssignmentElement) obj)
						.getAssignmentString();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
