package org.eventb.internal.ui.eventbeditor.editpage;

public class ElementRelUISpecRegistry extends AbstractElementRelUISpecRegistry {

	private static IElementRelUISpecRegistry instance = null;

	private ElementRelUISpecRegistry() {
		// Singleton: private to hide contructor
	}

	public static IElementRelUISpecRegistry getDefault() {
		if (instance == null)
			instance = new ElementRelUISpecRegistry();
		return instance;
	}

}
