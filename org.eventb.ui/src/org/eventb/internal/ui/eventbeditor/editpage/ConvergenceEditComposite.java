package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class ConvergenceEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	private final String ORDINARY = "ordinary";

	private final String CONVERGENT = "convergent";

	private final String ANTICIPATED = "anticipated";

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		Convergence convergence = event.getConvergence();
		if (convergence == Convergence.ORDINARY)
			return ORDINARY;
		if (convergence == Convergence.CONVERGENT)
			return CONVERGENT;
		if (convergence == Convergence.ANTICIPATED)
			return ANTICIPATED;
		return ORDINARY;
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
			if (newValue.equals(ORDINARY))
				event.setConvergence(Convergence.ORDINARY,
						new NullProgressMonitor());
			else if (newValue.equals(CONVERGENT))
				event.setConvergence(Convergence.CONVERGENT,
						new NullProgressMonitor());
			else if (newValue.equals(ANTICIPATED))
				event.setConvergence(Convergence.ANTICIPATED,
						new NullProgressMonitor());
		}
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		event.setConvergence(Convergence.ORDINARY, monitor);
	}

	@Override
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		return new String[] { ORDINARY, CONVERGENT, ANTICIPATED };
	}

	@Override
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(
				EventBAttributes.CONVERGENCE_ATTRIBUTE,
				new NullProgressMonitor());
	}

}
