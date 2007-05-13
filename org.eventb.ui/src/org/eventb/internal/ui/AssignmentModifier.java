package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IAssignmentElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class AssignmentModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IAssignmentElement) {
			IAssignmentElement aElement = (IAssignmentElement) element;
			String assignmentString = null;
			try {
				assignmentString = aElement.getAssignmentString();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			if (assignmentString == null || !assignmentString.equals(text))
				aElement.setAssignmentString(text, new NullProgressMonitor());
		}
		return;
	}

}
