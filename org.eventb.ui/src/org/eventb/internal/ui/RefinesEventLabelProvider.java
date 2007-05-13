package org.eventb.internal.ui;

import org.eventb.core.IRefinesEvent;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class RefinesEventLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IRefinesEvent) {
			return ((IRefinesEvent) obj).getAbstractEventLabel();
		}
		return null;
	}
}
