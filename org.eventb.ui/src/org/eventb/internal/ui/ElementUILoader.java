package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IRodinElement;

public class ElementUILoader {

	private static Collection<ElementUI> elementUIs;

	private final static String ELEMENTUI_ID = EventBUIPlugin.PLUGIN_ID
			+ ".elementui";

	public static Collection<ElementUI> getElementUIs() {
		if (elementUIs == null)
			elementUIs = internalGetElementUIs();
		return elementUIs;
	}

	private static Collection<ElementUI> internalGetElementUIs() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(ELEMENTUI_ID);
		IExtension[] extensions = extensionPoint.getExtensions();

		ArrayList<ElementUI> result = new ArrayList<ElementUI>();

		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				String name = element.getName();

				if (name.equals("element")) {
					String namespace = element.getNamespace();
					Bundle bundle = Platform.getBundle(namespace);
					try {
						String icon = element.getAttribute("icon");

						Class clazz = bundle.loadClass(element
								.getAttribute("class"));

						Class classObject = getSubclass(clazz,
								IRodinElement.class);

						ElementUI elementUI = new ElementUI(namespace, icon, classObject);
						result.add(elementUI);

					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				}
			}

		}
		return result;
	}

	// Code extracted to suppress spurious warning about unsafe type cast.
	@SuppressWarnings("unchecked")
	private static Class getSubclass(Class clazz, Class subClass) {
		return clazz.asSubclass(subClass);
	}

}
