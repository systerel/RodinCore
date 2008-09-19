package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.UIUtils;
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
		UIUtils.setStringAttribute(element, UIUtils
				.getLabelAttributeFactory(element), text, monitor);
	}

}
