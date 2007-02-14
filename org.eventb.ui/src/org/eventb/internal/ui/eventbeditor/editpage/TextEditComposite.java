package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.TimerText;

public abstract class TextEditComposite extends DefaultEditComposite {

	public void createComposite(FormToolkit toolkit, Composite parent, int style) {
		Text text = toolkit.createText(parent, getValue(), style);
		setControl(text);
		text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		new EventBMath(text);
		new TimerText(text, 1000) {
			@Override
			protected void response() {
				setValue();
			}

		};
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				internalPack();
			}

		});
	}
}
