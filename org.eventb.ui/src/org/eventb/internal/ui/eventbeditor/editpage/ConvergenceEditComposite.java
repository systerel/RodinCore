package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.CCombo;
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.RodinDBException;

public class ConvergenceEditComposite extends CComboEditComposite {

	private final String ORDINARY = "ORDINARY";

	private final String CONVERGENT = "CONVERGENT";

	private final String ANTICIPATED = "ANTICIPATED";

	@Override
	public String getValue() {
		IEvent event = (IEvent) element;
		try {
			Convergence convergence = event.getConvergence();
			if (convergence == Convergence.ORDINARY)
				return ORDINARY;
			if (convergence == Convergence.CONVERGENT)
				return CONVERGENT;
			if (convergence == Convergence.ANTICIPATED)
				return ANTICIPATED;
			return ORDINARY;
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "false";
	}

	@Override
	public void refresh() {
		CCombo combo = (CCombo) control;
		if (combo.getItemCount() != 3) {
			combo.removeAll();
			combo.add(ORDINARY);
			combo.add(CONVERGENT);
			combo.add(ANTICIPATED);
		}
		super.refresh();
	}

	@Override
	public void setValue() {
		assert element instanceof IEvent;
		CCombo combo = (CCombo) control;
		IEvent event = (IEvent) element;
		String str = combo.getText();

		if (!getValue().equals(str)) {
			try {
				if (str.equals(ORDINARY))
					event.setConvergence(Convergence.ORDINARY,
							new NullProgressMonitor());
				else if (str.equals(CONVERGENT))
					event.setConvergence(Convergence.CONVERGENT,
							new NullProgressMonitor());
				else if (str.equals(ANTICIPATED))
					event.setConvergence(Convergence.ANTICIPATED,
							new NullProgressMonitor());
			} catch (RodinDBException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}

		}
	}

}
