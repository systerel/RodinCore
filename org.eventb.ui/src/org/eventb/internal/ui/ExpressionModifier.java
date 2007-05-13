package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IExpressionElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ExpressionModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IExpressionElement) {
			IExpressionElement eElement = (IExpressionElement) element;
			String expressionString = null;
			try {
				expressionString = eElement.getExpressionString();
			}
			catch (RodinDBException e) {
				// Do nothing
			}
			if (expressionString == null || !expressionString.equals(text))
				eElement.setExpressionString(text, new NullProgressMonitor());
		}
		return;
	}

}
