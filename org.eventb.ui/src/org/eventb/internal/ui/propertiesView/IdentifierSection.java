package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class IdentifierSection extends TextSection {

	@Override
	String getLabel() {
		return "Identifier";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof IIdentifierElement) {
			return ((IIdentifierElement) element).getIdentifierString();
		}
		return null;
	}


	@Override
	void setStyle() {
		style = SWT.SINGLE;
		math = true;
	}
	
	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, UIUtils
				.getIdentifierAttributeFactory(element), text, monitor);
	}

}
