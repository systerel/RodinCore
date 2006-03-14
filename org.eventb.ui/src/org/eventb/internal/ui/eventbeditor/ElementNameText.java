package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Text;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ElementNameText extends ElementText {

	SyntheticViewSection section;
	
	public ElementNameText(Text text, IRodinElement element, SyntheticViewSection section) {
		super(text, element);
		this.section = section;
	}

	@Override
	public void commit() {
		try {
			IRodinElement parent = element.getParent();
			String type = element.getElementType();
			section.setUpToDate();
			((IInternalElement) element).rename(text.getText(), false, null);
			IRodinElement [] elements = ((IParent) parent).getChildrenOfType(type);
			for (int i = 0; i < elements.length; i++) {
				if (elements[i].getElementName().equals(text.getText())) {
					this.setElement(elements[i]);
					break;
				}
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

}
