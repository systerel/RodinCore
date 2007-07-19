package org.eventb.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class EventBSharedColor {
	
	public static Color getRequiredFieldBackgroundColor(Control control) {
		Display display = control.getDisplay();
		return display.getSystemColor(SWT.COLOR_YELLOW);
	}
}
