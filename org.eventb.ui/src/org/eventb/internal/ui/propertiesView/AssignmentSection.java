package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IAssignmentElement;
import org.rodinp.core.RodinDBException;

public class AssignmentSection extends TextSection {

	@Override
	String getLabel() {
		return "Assignment";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof IAssignmentElement) {
			IAssignmentElement aElement = (IAssignmentElement) element;
			return aElement.getAssignmentString();
		}
		return null;
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = true;
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		if (element instanceof IAssignmentElement) {
			IAssignmentElement aElement = (IAssignmentElement) element;
			if (!aElement.getAssignmentString().equals(text))
				aElement.setAssignmentString(text, monitor);
		}
	}

}
