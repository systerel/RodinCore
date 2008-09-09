package org.eventb.internal.ui;

import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.SeesContextNameAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SeesContextModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof ISeesContext) {
			ISeesContext aElement = (ISeesContext) element;
			IAttributeFactory factory = new SeesContextNameAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}

}
