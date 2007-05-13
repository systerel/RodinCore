package org.eventb.internal.ui;

import org.eventb.core.IExpressionElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class ExpressionLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IExpressionElement) {
			return ((IExpressionElement) obj).getExpressionString();
		}
		return null;
	}

}
