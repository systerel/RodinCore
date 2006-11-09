package org.eventb.internal.ui;

import org.eventb.core.IExpressionElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class ExpressionLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IExpressionElement) {
			try {
				return ((IExpressionElement) obj).getExpressionString();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
