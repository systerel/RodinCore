package org.eventb.internal.ui;

import org.eventb.core.ILabeledElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class LabelLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof ILabeledElement) {
			return ((ILabeledElement) obj).getLabel();
		}
		return null;
	}

}
