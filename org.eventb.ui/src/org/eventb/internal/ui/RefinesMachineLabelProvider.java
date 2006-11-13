package org.eventb.internal.ui;

import org.eventb.core.IRefinesMachine;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class RefinesMachineLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IRefinesMachine) {
			try {
				return ((IRefinesMachine) obj).getAbstractMachineName(null);
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
