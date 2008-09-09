package org.eventb.internal.ui;

import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.eventbeditor.editpage.ActionLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class LabelModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof ILabeledElement) {
			ILabeledElement aElement = (ILabeledElement) element;
			IAttributeFactory factory = new ActionLabelAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}
	
}
