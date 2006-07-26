package org.eventb.internal.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

public class EventBControl implements IPropertyChangeListener {

	Control control;

	public EventBControl(Control control) {
		this.control = control;
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		control.setFont(font);

		JFaceResources.getFontRegistry().addListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			control.setFont(font);
		}
	}

	public Control getControl() {
		return control;
	}

	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		control.dispose();
	}
	
}
