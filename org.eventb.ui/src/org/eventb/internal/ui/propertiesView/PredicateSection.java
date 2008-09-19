package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.PredicateAttributeFactory;
import org.rodinp.core.RodinDBException;

public class PredicateSection extends TextSection {

	@Override
	String getLabel() {
		return "Predicate";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof IPredicateElement) {
			IPredicateElement pElement = (IPredicateElement) element;
			return pElement.getPredicateString();
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
		UIUtils.setStringAttribute(element, new PredicateAttributeFactory(), text, monitor);
	}

}
