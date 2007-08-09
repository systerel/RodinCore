package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.TimerStyledText;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class TextEditComposite extends AbstractEditComposite {

	protected StyledText text;
	private Button undefinedButton;
	protected int style = SWT.MULTI | SWT.BORDER;

	public TextEditComposite(IAttributeUISpec uiSpec) {
		super(uiSpec);
	}

	@Override
	public void initialise() {
		try {
			String value = uiSpec.getAttributeFactory().getValue(element,
					new NullProgressMonitor());
			displayValue(value);
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	private void displayValue(String value) {
		if (text == null) {
			if (undefinedButton != null) {
				undefinedButton.dispose();
				undefinedButton = null;
			}
			text = new StyledText(composite, style);
			new EventBStyledText(text) {

				@Override
				protected void commit() {
					try {
						uiSpec.getAttributeFactory().setValue(element,
								text.getText(), new NullProgressMonitor());
					} catch (RodinDBException e) {
						EventBUIExceptionHandler.handleSetAttributeException(e);
					}
					super.commit();
				}

			};
			text.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_DARK_GREEN));
			new TimerStyledText(text, 200) {
				@Override
				protected void response() {
					if (text.isFocusControl())
						try {
							uiSpec.getAttributeFactory().setValue(element,
									text.getText(), new NullProgressMonitor());
						} catch (RodinDBException e) {
							EventBUIExceptionHandler
									.handleSetAttributeException(e);
						}
				}

			};
			this.getFormToolkit().paintBordersFor(composite);
		}
		if (!text.getText().equals(value))
			text.setText(value);
	}

	public void setUndefinedValue() {
		FormToolkit toolkit = this.getFormToolkit();
		if (undefinedButton != null)
			return;

		if (text != null)
			text.dispose();

		undefinedButton = toolkit
				.createButton(composite, "UNDEFINED", SWT.PUSH);
		undefinedButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				setDefaultValue(fEditor);
			}

		});
	}

	@Override
	public void setSelected(boolean selection) {
		Control control = text == null ? undefinedButton : text;
		if (selection)
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	public void setDefaultValue(IEventBEditor<?> editor) {
		try {
			uiSpec.getAttributeFactory().setDefaultValue(editor, element,
					new NullProgressMonitor());
			if (text != null)
				text.setFocus();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

}
