package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class IdentifierAttributeFactory implements IAttributeFactory {

	protected String defaultPrefix = "prefix";

	public abstract void setPrefix();

	public void createDefaulAttribute(IEventBEditor<?> editor,
			IInternalElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IIdentifierElement)) {
			return;
		}
		setPrefix();
		IIdentifierElement iElement = (IIdentifierElement) element;
		String identifier = UIUtils.getFreeElementIdentifier(editor,
				(IInternalParent) element.getParent(), iElement
						.getElementType(), defaultPrefix);
		iElement.setIdentifierString(identifier, monitor);
	}

}
