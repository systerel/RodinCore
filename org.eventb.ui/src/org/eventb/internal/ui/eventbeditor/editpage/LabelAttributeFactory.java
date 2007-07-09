package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class LabelAttributeFactory implements IAttributeFactory {

	protected String defaultPrefix = "prefix";

	protected abstract void setPrefix();

	public void createDefaulAttribute(IEventBEditor<?> editor,
			IInternalElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof ILabeledElement)) {
			return;
		}
		setPrefix();
		ILabeledElement lElement = (ILabeledElement) element;
		String label = UIUtils.getFreeElementLabel(editor,
				(IInternalParent) element.getParent(), lElement
						.getElementType(), defaultPrefix);
		lElement.setLabel(label, monitor);
	}

}
