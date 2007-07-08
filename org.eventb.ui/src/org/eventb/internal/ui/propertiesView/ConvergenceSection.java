package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.RodinDBException;

public class ConvergenceSection extends CComboSection {

	private static final String ORDINARY = "ORDINARY";

	private static final String CONVERGENT = "CONVERGENT";

	private static final String ANTICIPATED = "ANTICIPATED";

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
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		IConvergenceElement cElement = (IConvergenceElement) element;
		Convergence convergence = null;
		try {
			convergence = cElement.getConvergence();
		} catch (RodinDBException e) {
			// Do nothing
		}
		if (text.equals(ORDINARY)
				&& (convergence == null || convergence != Convergence.ORDINARY)) {
			cElement.setConvergence(Convergence.ORDINARY, monitor);
		} else if (text.equals(CONVERGENT)
				&& (convergence == null || convergence != Convergence.CONVERGENT)) {
			cElement.setConvergence(Convergence.CONVERGENT, monitor);
		} else if (text.equals(ANTICIPATED)
				&& (convergence == null || convergence != Convergence.ANTICIPATED)) {
			cElement.setConvergence(Convergence.ANTICIPATED, monitor);
		}
	}

}
