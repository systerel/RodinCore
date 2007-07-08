package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.Action;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ShowAbstractInvariant extends Action {
	IRodinFile abstractFile;

	public ShowAbstractInvariant(IRodinFile abstractFile) {
		this.abstractFile = abstractFile;
		this.setText(abstractFile.getBareName());
		this.setToolTipText("Show the abstract invariant");
		this.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_REFINES_PATH));
	}

	@Override
	public void run() {
		IRodinElement[] elements;
		try {
			elements = abstractFile.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}

		if (elements.length != 0)
			UIUtils.linkToEventBEditor(elements[0]);
		else
			UIUtils.linkToEventBEditor(abstractFile);

	}

}
