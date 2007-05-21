package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class InheritedEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	private final String TRUE = "true";

	private final String FALSE = "false";

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		return event.isInherited() ? TRUE : FALSE;
	}

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IEvent;

		IEvent event = (IEvent) element;
		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			event.setInherited(newValue.equalsIgnoreCase(TRUE),
					new NullProgressMonitor());
		}
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		event.setInherited(false, monitor);
	}

	@Override
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		return new String[] { TRUE, FALSE };
	}

	@Override
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.INHERITED_ATTRIBUTE, monitor);
	}

}
