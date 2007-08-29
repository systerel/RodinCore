package org.eventb.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eventb.ui.EventBUIPlugin;

public class EventBSharedColor implements IEventBSharedColor {
	
	private static IEventBSharedColor instance;
	
	private Display display;
	
	private EventBSharedColor() {
		// Singleton: Private constructor
		display = EventBUIPlugin.getActiveWorkbenchShell().getDisplay();
	}

	public static IEventBSharedColor getDefault() {
		if (instance == null)
			instance = new EventBSharedColor();
		return instance;
	}
	
	public static Color getRequiredFieldBackgroundColor(Control control) {
		Display display = control.getDisplay();
		return display.getSystemColor(SWT.COLOR_YELLOW);
	}

	public Color getColor(String key) {
		if (key.equals(IEventBSharedColor.DIRTY_STATE))
			display.getSystemColor(SWT.COLOR_YELLOW);
		if (key.equals(IEventBSharedColor.BOX_BORDER))
			display.getSystemColor(SWT.COLOR_RED);
		return null;
	}

}
