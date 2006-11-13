package org.eventb.internal.ui;

import org.eventb.core.IExpressionElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ExpressionModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IExpressionElement) {
			((IExpressionElement) element).setExpressionString(text);
		}
		return;
	}

}
