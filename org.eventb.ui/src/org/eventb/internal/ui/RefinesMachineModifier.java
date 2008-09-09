package org.eventb.internal.ui;

import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesMachineAbstractMachineNameAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RefinesMachineModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IRefinesMachine) {
			IRefinesMachine aElement = (IRefinesMachine) element;
			IAttributeFactory factory = new RefinesMachineAbstractMachineNameAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}
}
