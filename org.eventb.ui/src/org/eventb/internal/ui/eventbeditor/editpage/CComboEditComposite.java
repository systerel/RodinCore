package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.rodinp.core.RodinDBException;

public class CComboEditComposite extends DefaultEditComposite implements
		IEditComposite {

	protected final String UNDEFINED = "----- UNDEFINED -----";
	
	protected CCombo combo;
	protected Button undefinedButton;

	IAttributeEditor attributeEditor;
	
	public CComboEditComposite(IAttributeEditor attributeEditor) {
		this.attributeEditor = attributeEditor;
	}

	@Override
	public void initialise() {
		try {
			String value = attributeEditor.getAttribute(element,
					new NullProgressMonitor());
			createCombo();
			combo.setText(value);
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	@Override
	public void setSelected(boolean selection) {
		Control control = combo == null ? undefinedButton : combo;
		if (selection)
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	public void setUndefinedValue() {
		if (combo == null)
			createCombo();
		
		combo.setText(UNDEFINED);
	}

	private void createCombo() {
		if (combo != null)
			combo.removeAll();
		else {
			combo = new CCombo(composite, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					if (combo.getText().equals(UNDEFINED)) {
						try {
							attributeEditor.removeAttribute(element,
									new NullProgressMonitor());
						} catch (RodinDBException e1) {
							EventBUIExceptionHandler.handleRemoveAttribteException(e1);
						}
					} else {
						try {
							attributeEditor.setAttribute(element, combo
									.getText(), new NullProgressMonitor());
						} catch (RodinDBException exception) {
							EventBUIExceptionHandler
									.handleSetAttributeException(exception);
						}
					}
				}
			});
			this.getFormToolkit().paintBordersFor(composite);
		}
		
		combo.add(UNDEFINED);
		String[] values = attributeEditor.getPossibleValues(element,
				new NullProgressMonitor());
		for (String value : values) {
			combo.add(value);
		}
	}

	public void setDefaultValue() {
		try {
			attributeEditor.setDefaultAttribute(element, new NullProgressMonitor());
			if (combo != null)
				combo.setFocus();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

}
