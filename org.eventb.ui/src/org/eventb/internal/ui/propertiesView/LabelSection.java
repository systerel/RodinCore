package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.RodinDBException;

public class LabelSection extends TextSection {

	@Override
	String getLabel() {
		return "Label";
	}

	@Override
	String getText() throws RodinDBException {
		ILabeledElement lElement = (ILabeledElement) element;
		return lElement.getLabel();
	}

	@Override
	void setText(String text) throws RodinDBException {
		ILabeledElement lElement = (ILabeledElement) element;
		if (!lElement.getLabel().equals(text))
			lElement.setLabel(text, new NullProgressMonitor());
	}

}
