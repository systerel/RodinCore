package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IRefinesMachine;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RefinesMachineModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IRefinesMachine) {
			IRefinesMachine refinesMachine = (IRefinesMachine) element;
			if (!refinesMachine.getAbstractMachine().equals(text))
				refinesMachine.setAbstractMachineName(text,
						new NullProgressMonitor());
		}
		return;
	}

}
