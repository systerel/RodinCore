package org.eventb.internal.ui;

import org.eventb.core.IRefinesEvent;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class RefinesEventLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IRefinesEvent) {
			try {
				return ((IRefinesEvent) obj).getAbstractEventLabel();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}
