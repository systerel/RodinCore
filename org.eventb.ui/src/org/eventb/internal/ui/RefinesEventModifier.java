package org.eventb.internal.ui;

import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesEventAbstractEventLabelAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RefinesEventModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IRefinesEvent) {
			IRefinesEvent aElement = (IRefinesEvent) element;
			IAttributeFactory factory = new RefinesEventAbstractEventLabelAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}

}
