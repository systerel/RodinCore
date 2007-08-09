package org.eventb.ui.eventbeditor.editpage.tests;

import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.eventbeditor.editpage.AbstractElementRelUISpecRegistry;
import org.eventb.ui.tests.EventBUITestsPlugin;
public class ElementRelUISpecTestRegistry extends AbstractElementRelUISpecRegistry {

	public static ElementRelUISpecTestRegistry instance = null;

	private final static String ELEMENT_UI_SPECS_TEST_ID = EventBUITestsPlugin.PLUGIN_ID
			+ ".elementUISpecs";

	private ElementRelUISpecTestRegistry() {
		// Singleton: private to hide contructor
		extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				ELEMENT_UI_SPECS_TEST_ID);
		specRegistry = ElementSpecTestRegistry.getDefault();
	}

	public static ElementRelUISpecTestRegistry getDefault() {
		if (instance == null)
			instance = new ElementRelUISpecTestRegistry();
		return instance;
	}
	
}
