package org.eventb.internal.ui;

import org.eventb.core.ILabeledElement;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class LabelLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) obj).getLabel();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
