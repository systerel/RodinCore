package org.eventb.ui.eventbeditor.editpage.tests;

import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.eventbeditor.editpage.AbstractAttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecRegistry;
import org.eventb.ui.tests.EventBUITestsPlugin;


public class AttributeRelUISpecTestRegistry extends AbstractAttributeRelUISpecRegistry {

	public static IAttributeRelUISpecRegistry instance = null;

	private final static String ELEMENT_UI_SPECS_TEST_ID = EventBUITestsPlugin.PLUGIN_ID
			+ ".elementUISpecs";

	private AttributeRelUISpecTestRegistry() {
		// Singleton: private to hide contructor
		extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				ELEMENT_UI_SPECS_TEST_ID);
		specRegistry = ElementSpecTestRegistry.getDefault();
		
	}

	public static IAttributeRelUISpecRegistry getDefault() {
		if (instance == null)
			instance = new AttributeRelUISpecTestRegistry();
		return instance;
	}
	
}
