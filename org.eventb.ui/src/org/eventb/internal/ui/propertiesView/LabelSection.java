package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.RodinDBException;

public class LabelSection extends TextSection {

	@Override
	String getLabel() {
		return "Label";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof ILabeledElement) {
			ILabeledElement lElement = (ILabeledElement) element;
			return lElement.getLabel();
		}
		return null;
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		ILabeledElement lElement = (ILabeledElement) element;
		if (!lElement.getLabel().equals(text))
			lElement.setLabel(text, monitor);
	}

}
