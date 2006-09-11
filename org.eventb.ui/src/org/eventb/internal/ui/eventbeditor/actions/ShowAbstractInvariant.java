package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.Action;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ShowAbstractInvariant extends Action {
	IRodinFile abstractFile;

	public ShowAbstractInvariant(IRodinFile abstractFile) {
		this.abstractFile = abstractFile;
		this.setText(EventBPlugin.getComponentName(abstractFile
				.getElementName()));
		this.setToolTipText("Show the abstract invariant");
		this.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_REFINES));
	}

	public void run() {
		try {
			IRodinElement[] elements = abstractFile
					.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			
			if (elements.length != 0) {
				UIUtils.linkToEventBEditor(elements[0]);
			} else
				UIUtils.linkToEventBEditor(abstractFile);

		} catch (RodinDBException e) {
			e.printStackTrace();
		}

	}

}
