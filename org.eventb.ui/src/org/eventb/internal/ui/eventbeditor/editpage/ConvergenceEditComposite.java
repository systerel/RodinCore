package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.RodinDBException;

public class ConvergenceEditComposite extends CComboEditComposite {

	private final String ORDINARY = "ORDINARY";

	private final String CONVERGENT = "CONVERGENT";

	private final String ANTICIPATED = "ANTICIPATED";

	public String getValue() throws RodinDBException {
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

	public void setValue() {
		assert element instanceof IEvent;
		if (combo == null)
			return;
		
		IEvent event = (IEvent) element;
		String str = combo.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		
		if (value == null || !value.equals(str)) {
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
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void displayValue(String value) {
		if (combo == null) {
			if (undefinedButton != null) {
				undefinedButton.dispose();
				undefinedButton = null;
			}
			
			combo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
			combo.add(ORDINARY);
			combo.add(CONVERGENT);
			combo.add(ANTICIPATED);
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					setValue();
				}

			});
		}
		combo.setText(value);
	}

	@Override
	public void setDefaultValue() {
		IEvent event = (IEvent) element;
		try {
			event.setConvergence(Convergence.ORDINARY,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setDefaultValue();
	}

}
