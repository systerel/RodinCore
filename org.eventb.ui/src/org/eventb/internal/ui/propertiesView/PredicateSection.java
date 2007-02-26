package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.RodinDBException;

public class PredicateSection extends TextSection {

	@Override
	String getLabel() {
		return "Predicate";
	}

	@Override
	String getText() throws RodinDBException {
		IPredicateElement pElement = (IPredicateElement) element;
		return pElement.getPredicateString();
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
	}

	@Override
	void setText(String text) throws RodinDBException {
		IPredicateElement pElement = (IPredicateElement) element;
		if (!pElement.getPredicateString().equals(text))
			pElement.setPredicateString(text, new NullProgressMonitor());
	}

}
