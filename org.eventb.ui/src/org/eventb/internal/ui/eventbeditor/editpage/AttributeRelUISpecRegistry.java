package org.eventb.internal.ui.eventbeditor.editpage;

public class AttributeRelUISpecRegistry extends
		AbstractAttributeRelUISpecRegistry {

	public static IAttributeRelUISpecRegistry instance = null;

	private AttributeRelUISpecRegistry() {
		// Singleton: private to hide contructor
	}

	public static IAttributeRelUISpecRegistry getDefault() {
		if (instance == null)
			instance = new AttributeRelUISpecRegistry();
		return instance;
	}

}
