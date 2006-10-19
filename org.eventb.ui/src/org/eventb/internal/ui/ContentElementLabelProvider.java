package org.eventb.internal.ui;

import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class ContentElementLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IInternalElement) {
			try {
				return ((IInternalElement) obj).getContents();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return "";
			}
		}
		return "";
	}

}
