/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.osgi.framework.Bundle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is a singleton class that manages the UI information
 *         related to different elements that appeared in the UI
 */
public class ElementUIRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.ui.elementui"</code>).
	private final static String ELEMENTUI_ID = EventBUIPlugin.PLUGIN_ID
			+ ".elementui";

	// The default priority (value 10000)
	private static final int DEFAULT_PRIORITY = 10000;

	// The default label (value empty string)
	private static final String DEFAULT_LABEL = ""; //$NON-NLS-1$

	// The static instance of this singleton class
	private static ElementUIRegistry instance;

	// The registry stored Element UI information
	private HashMap<Class, ElementUIInfo> registry;

	/**
	 * @author htson
	 *         <p>
	 *         This private helper class stored the UI information for a single
	 *         element
	 */
	private class ElementUIInfo {
		// Configuration information related to the element
		private IConfigurationElement configuration;

		/**
		 * The image descriptor for the element (which is lazy loading using
		 * <code>configuration</code>)
		 */
		private ImageDescriptor imageDesc;

		/**
		 * Constructor: Stored the configuration element in order to implement
		 * lazy loading
		 * <p>
		 * 
		 * @param configuration
		 *            a configuration element
		 */
		public ElementUIInfo(IConfigurationElement configuration) {
			this.configuration = configuration;
		}

		/**
		 * Getting the image descriptor of the element
		 * <p>
		 * 
		 * @return the image descriptor associated with the element
		 */
		public ImageDescriptor getImageDescriptor() {
			if (imageDesc == null)
				imageDesc = createImageDescriptor();
			return imageDesc;
		}

		/**
		 * A private help method which create the image decscriptor from the
		 * <code>configuration</code>
		 * <p>
		 * 
		 * @return the newly created image descriptor
		 */
		private ImageDescriptor createImageDescriptor() {
			String iconName = configuration.getAttributeAsIs("icon"); //$NON-NLS-1$
			String namespace = configuration.getNamespace();
			ImageDescriptor desc = EventBImage.getImageDescriptor(namespace,
					iconName);
			return desc;
		}

		/**
		 * Getting the label for an object
		 * <p>
		 * 
		 * @param obj
		 *            any object
		 * @return the label corresponding to the input object
		 */
		public String getLabel(Object obj) {
			return getLabel(obj, "labelAttribute", "labelProvider");
		}

		/**
		 * Getting the priority for an object
		 * <p>
		 * 
		 * @param obj
		 *            any object
		 * @return the priority corresponding to the input object
		 */
		public int getPriority(Object obj) {
			String priorityString = configuration.getAttributeAsIs("priority"); //$NON-NLS-1$
			try {
				return Integer.parseInt(priorityString);
			} catch (NumberFormatException e) {
				if (UIUtils.DEBUG)
					System.out
							.println("Priority must be a natural number, default priority assigned to "
									+ obj);
				UIUtils.log(e,
						"Priority must be a natural number, default priority assigned to "
								+ obj);
				return DEFAULT_PRIORITY;
			}
		}

		/**
		 * A private helper method which get the label for an object providing
		 * the label attribute and the label provider extension attribute
		 * <p>
		 * 
		 * @param obj
		 *            any object
		 * @param attribute
		 *            the string represents the label attribute extension
		 *            attribute
		 * @param provider
		 *            the string repressents the label provider extension
		 *            attribute
		 * @return the label according to the input object and the extension
		 *         names
		 */
		private String getLabel(Object obj, String attribute, String provider) {
			String labelAttribute = configuration.getAttributeAsIs(attribute);
			if (labelAttribute != null) {
				if (obj instanceof IInternalElement) {
					IInternalElement element = (IInternalElement) obj;
					// TODO Should use a generalised method to get an attribute.
					try {
						return element.getStringAttribute(labelAttribute,
								new NullProgressMonitor());
					} catch (RodinDBException e) {
						if (UIUtils.DEBUG) {
							System.out.println("Cannot get attribute "
									+ labelAttribute + " of " + obj);
							e.printStackTrace();
						}
						UIUtils.log(e, "Cannot get attribute " + labelAttribute
								+ " of " + obj);
					}
				}
			} else {
				// Try to use the label provider
				try {
					IElementLabelProvider labelProvider = (IElementLabelProvider) configuration
							.createExecutableExtension(provider); //$NON-NLS-1$
					return labelProvider.getLabel(obj);
				} catch (CoreException e) {
					if (UIUtils.DEBUG) {
						System.out
								.println("Cannot instantiate the label provider class "
										+ configuration
												.getAttributeAsIs("secondLabelProvider"));
						e.printStackTrace();
					}
				}
			}
			if (UIUtils.DEBUG) {
				System.out.println("No label information stored for " + obj);
			}
			return DEFAULT_LABEL;

		}

		/**
		 * Get a secondary label (used for the second column in Event-B Editable
		 * Tree Viewer)
		 * <p>
		 * 
		 * @param obj
		 *            any object
		 * @return the secondary label corresponding to the object
		 */
		public String getSecondaryLabel(Object obj) {
			return getLabel(obj, "secondLabelAttribute", "secondLabelProvider");
		}

	}

	/**
	 * A private constructor to prevent creating an instance of this class
	 * directly
	 */
	private ElementUIRegistry() {
		// Singleton to hide the constructor
	}

	/**
	 * Getting the default instance of this class (create a new instance of it
	 * does not exist before)
	 * <p>
	 * 
	 * @return An instance of this class
	 */
	public static ElementUIRegistry getDefault() {
		if (instance == null)
			instance = new ElementUIRegistry();
		return instance;
	}

	/**
	 * Initialises the registry using extensions to the element UI extension
	 * point
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		registry = new HashMap<Class, ElementUIInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(ELEMENTUI_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			String namespace = configuration.getNamespace();
			Bundle bundle = Platform.getBundle(namespace);
			String className = configuration.getAttributeAsIs("class"); //$NON-NLS-1$
			try {
				Class clazz = bundle.loadClass(className);
				ElementUIInfo oldInfo = registry.put(clazz, new ElementUIInfo(
						configuration));
				if (oldInfo != null) {
					registry.put(clazz, oldInfo);
					if (UIUtils.DEBUG) {
						System.out
								.println("Configuration is already exists for class "
										+ className
										+ ", ignore configuration with id "
										+ configuration.getAttributeAsIs("id")); // $NON-NLS-3$
					}
				} else {
					if (UIUtils.DEBUG) {
						System.out.println("Registered element UI for class "
								+ className);
					}
				}
			} catch (ClassNotFoundException e) {
				if (UIUtils.DEBUG) {
					System.out.println("Cannot find class " + className
							+ " in bundle " + namespace);
					e.printStackTrace();
				}
				UIUtils.log(e, "Cannot find class " + className + " in bundle "
						+ namespace);
			}

		}

	}

	/**
	 * Getting a image descriptor corresponding to an object
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the image descriptor correspoinding to the input object
	 */
	public synchronized ImageDescriptor getImageDescriptor(Object obj) {
		if (registry == null)
			loadRegistry();

		Set<Class> classes = registry.keySet();
		for (Class clazz : classes) {
			if (clazz.isInstance(obj))
				return registry.get(clazz).getImageDescriptor();
		}

		return null;
	}

	/**
	 * Getting the label corresponding to an object
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the label corresponding to the input object
	 */
	public synchronized String getLabel(Object obj) {
		if (registry == null)
			loadRegistry();

		Set<Class> classes = registry.keySet();
		for (Class clazz : classes) {
			if (clazz.isInstance(obj))
				return registry.get(clazz).getLabel(obj);
		}

		return DEFAULT_LABEL;
	}

	/**
	 * Getting the primary label corresponding to an object (which is used in
	 * the first column of Event-B Table Tree Viewer
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the primary label corresponding to the input object (this is
	 *         simply the same as the label of the object)
	 */
	public String getPrimaryLabel(Object obj) {
		return getLabel(obj);
	}

	/**
	 * Getting the secondary label corresponding to an object (which is used in
	 * the second column of Event-B Table Tree Viewer
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the secondary label corresponding to the input object
	 */
	public String getSecondaryLabel(Object obj) {
		if (registry == null)
			loadRegistry();

		Set<Class> classes = registry.keySet();
		for (Class clazz : classes) {
			if (clazz.isInstance(obj))
				return registry.get(clazz).getSecondaryLabel(obj);
		}

		return DEFAULT_LABEL;
	}

	/**
	 * Getting the priority of an object
	 * <p>
	 * 
	 * @param obj
	 *            any object
	 * @return the priority corresponding to the input object
	 */
	public int getPriority(Object obj) {
		if (registry == null)
			loadRegistry();

		Set<Class> classes = registry.keySet();
		for (Class clazz : classes) {
			if (clazz.isInstance(obj))
				return registry.get(clazz).getPriority(obj);
		}

		return DEFAULT_PRIORITY;
	}

}
