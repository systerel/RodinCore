package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class ConvergenceSection extends CComboSection {

	private static final String ORDINARY = "ORDINARY";

	private static final String CONVERGENT = "CONVERGENT";

	private static final String ANTICIPATED = "ANTICIPATED";

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(part instanceof IEventBEditor);
		editor = (IEventBEditor) part;
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof IEvent);
		this.element = (IEvent) input;
	}


	@Override
	String getLabel() {
		return "Conv.";
	}

	@Override
	String getText() throws RodinDBException {
		IConvergenceElement cElement = (IConvergenceElement) element;
		Convergence convergence = cElement.getConvergence();
		if (convergence == Convergence.ORDINARY)
			return ORDINARY;
		if (convergence == Convergence.CONVERGENT)
			return CONVERGENT;
		if (convergence == Convergence.ANTICIPATED)
			return ANTICIPATED;
		return "";
	}

	@Override
	void setData() {
		comboWidget.add(ORDINARY);
		comboWidget.add(CONVERGENT);
		comboWidget.add(ANTICIPATED);
	}

	@Override
	void setText(String text) throws RodinDBException {
		IConvergenceElement cElement = (IConvergenceElement) element;
		if (text.equals(ORDINARY)
				&& cElement.getConvergence() != Convergence.ORDINARY) {
			cElement.setConvergence(Convergence.ORDINARY,
					new NullProgressMonitor());
		} else if (text.equals(CONVERGENT)
				&& cElement.getConvergence() != Convergence.CONVERGENT) {
			cElement.setConvergence(Convergence.CONVERGENT,
					new NullProgressMonitor());
		} else if (text.equals(ANTICIPATED)
				&& cElement.getConvergence() != Convergence.ANTICIPATED) {
			cElement.setConvergence(Convergence.ANTICIPATED,
					new NullProgressMonitor());
		}		
	}

}
