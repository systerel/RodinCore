package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
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
		IAssignmentElement aElement = (IAssignmentElement) element;
		return aElement.getAssignmentString();
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = true;
	}

	@Override
	void setText(String text) throws RodinDBException {
		IAssignmentElement aElement = (IAssignmentElement) element;
		if (!aElement.getAssignmentString().equals(text))
			aElement.setAssignmentString(text, new NullProgressMonitor());
	}

}
