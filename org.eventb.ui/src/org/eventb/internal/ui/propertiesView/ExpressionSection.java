package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IExpressionElement;
import org.rodinp.core.RodinDBException;

public class ExpressionSection extends TextSection {

	@Override
	String getLabel() {
		return "Expression";
	}

	@Override
	String getText() throws RodinDBException {
		IExpressionElement eElement = (IExpressionElement) element;
		return eElement.getExpressionString();
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
	}

	@Override
	void setText(String text) throws RodinDBException {
		IExpressionElement eElement = (IExpressionElement) element;
		if (!eElement.getExpressionString().equals(text))
			eElement.setExpressionString(text, new NullProgressMonitor());
	}

}
