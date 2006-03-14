package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Text;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ElementContentText extends ElementText {

	SyntheticViewSection section;
	
	public ElementContentText(Text text, IRodinElement element, SyntheticViewSection section) {
		super(text, element);
		this.section = section;
	}

	@Override
	public void commit() {
		try {
			section.setUpToDate();
			((IInternalElement) element).setContents(text.getText());
 		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

}
