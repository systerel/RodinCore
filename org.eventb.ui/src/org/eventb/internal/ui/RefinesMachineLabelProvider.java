package org.eventb.internal.ui;

import org.eventb.core.IRefinesMachine;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class RefinesMachineLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) throws RodinDBException {
		if (obj instanceof IRefinesMachine) {
			return ((IRefinesMachine) obj).getAbstractMachineName();
		}
		return null;
	}

}
