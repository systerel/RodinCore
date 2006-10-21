package org.eventb.internal.ui.eventbeditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eventb.internal.ui.UIUtils;

public class Messages {
	private static final String BUNDLE_NAME = "org.eventb.internal.ui.eventbeditor.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			// Log the error. Report the error if in debug mode
			String message = "Cannot find the externalised string for key "
					+ key;
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug(message);
			UIUtils.log(e, message);
			return '!' + key + '!';
		}
	}
}
