package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.TimerText;
import org.rodinp.core.RodinDBException;

public abstract class TextEditComposite extends DefaultEditComposite {

	public void refresh() {
		Text text = (Text) control;
		String str;
		try {
			str = getValue();
			if (!text.getText().equals(str)) {
				text.setText(str);
			}
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
		internalPack();
	}

	public void initialise() {
		Text text = (Text) control;
		try {
			text.setText(getValue());
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	public void createMainComposite(FormToolkit toolkit, Composite parent, int style) {
		Text text = toolkit.createText(parent, "", style);
		setControl(text);
		initialise();
		text.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_DARK_GREEN));
		new EventBMath(text);
		new TimerText(text, 1000) {
			@Override
			protected void response() {
				setValue();
			}

		};
	}

	public void setUndefinedValue() {
		Text text = (Text) control;
		text.setText("----- UNDEFINED -----");
		text.setEditable(false);
	}

	@Override
	public void setSelected(boolean selection) {
		Text text = (Text) control;
		if (selection)
			text
					.setBackground(text.getDisplay().getSystemColor(
							SWT.COLOR_GRAY));
		else {
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

}
