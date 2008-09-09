package org.eventb.internal.ui;

import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.VariableIdentifierAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class IdentifierModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IIdentifierElement) {
			IIdentifierElement aElement = (IIdentifierElement) element;
			IAttributeFactory factory = new VariableIdentifierAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}

}
