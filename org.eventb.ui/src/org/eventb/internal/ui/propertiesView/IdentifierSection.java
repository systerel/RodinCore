package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.rodinp.core.RodinDBException;

public class IdentifierSection extends TextSection {

	@Override
	String getLabel() {
		return "Identifier";
	}

	@Override
	String getText() throws RodinDBException {
		return ((IIdentifierElement) element).getIdentifierString();
	}

	@Override
	void setStyle() {
		// Do nothing, by default it is SWT.SINGLE
	}

	@Override
	void setText(String text) throws RodinDBException {
		IIdentifierElement iElement = (IIdentifierElement) element;
		if (!iElement.getIdentifierString().equals(text)) {
			iElement.setIdentifierString(text, new NullProgressMonitor());
		}
	}

}
