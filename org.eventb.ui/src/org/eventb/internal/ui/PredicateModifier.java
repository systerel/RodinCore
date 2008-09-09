package org.eventb.internal.ui;

import org.eventb.core.IPredicateElement;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.PredicateAttributeFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class PredicateModifier extends AbstractModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IPredicateElement) {
			IPredicateElement aElement = (IPredicateElement) element;
			IAttributeFactory factory = new PredicateAttributeFactory();
			doModify(factory, aElement, text);
		}
		return;
	}

}
