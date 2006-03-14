package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Text;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ElementNameContentTexts {
	
	SyntheticViewSection section;
	ElementText name;
	ElementText content;

	public ElementNameContentTexts(Text nameText, Text contentText, IRodinElement element, final SyntheticViewSection section) {
		this.section = section;
		name = new ElementText(nameText, element) {

			/* (non-Javadoc)
			 * @see org.eventb.internal.ui.eventbeditor.ElementText#commit()
			 */
			@Override
			public void commit() {
				commitName();
			}
		};
		
		content = new ElementContentText(contentText, element, section);
	}

	public void commitName() {
		IRodinElement parent = name.element.getParent();
		String type = name.element.getElementType();
		section.setUpToDate();
		try {
			((IInternalElement) name.element).rename(name.text.getText(), false, null);
		
			IRodinElement [] elements = ((IParent) parent).getChildrenOfType(type);
			for (int i = 0; i < elements.length; i++) {
				if (elements[i].getElementName().equals(name.text.getText())) {
					name.setElement(elements[i]);
					content.setElement(elements[i]);
					break;
				}
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
}
