package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
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
		if (element == null)
			return null;
		if (element instanceof IExpressionElement) {
			IExpressionElement eElement = (IExpressionElement) element;
			if (eElement == null)
				return null;
			return eElement.getExpressionString();
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
		if (element instanceof IExpressionElement) {
			IExpressionElement eElement = (IExpressionElement) element;
			if (!eElement.getExpressionString().equals(text))
				eElement.setExpressionString(text, monitor);
		}
	}

}
