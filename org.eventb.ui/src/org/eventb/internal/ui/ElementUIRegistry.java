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

package org.eventb.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IElementLabelProvider;
import org.eventb.ui.IElementModifier;
import org.eventb.ui.NullModifier;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is a singleton class that manages the UI information
 *         related to different elements that appeared in the UI.
 */
public class ElementUIRegistry implements IElementUIRegistry {

	// Debugging flag for Element UI Registry.
	public static boolean DEBUG = false;
	
	// Debugging prefix for Element UI Registry.
	public final static String DEBUG_PREFIX = "*** Element UI Registry *** "; //$NON-NLS-1$

	// The identifier of the extension point (value
	// <code>"org.eventb.ui.elementui"</code>).
	private final static String ELEMENTUI_ID = EventBUIPlugin.PLUGIN_ID
			+ ".elementui"; //$NON-NLS-1$

	// The default priority (value 10000)
	private static final int DEFAULT_PRIORITY = 10000;

	// The default label (value empty string)
	private static final String DEFAULT_LABEL = ""; //$NON-NLS-1$

	// The default value for edit-able attribute
	private static final boolean DEFAULT_EDITABLE = true;

	// The static instance of this singleton class
	private static IElementUIRegistry instance;

	// The registry stored Element UI information
	private Map<IElementType<?>, ElementUIInfo> registry;

	/**
	 * @author htson
	 *         <p>
	 *         This private helper class stored the UI information for a single
	 *         element.
	 */
	private class ElementUIInfo {
		// Configuration information related to the element
		private IConfigurationElement configuration;

		private HashMap<String, IElementLabelProvider> providers;

		private HashMap<String, IElementModifier> modifiers;

		/**
		 * The image descriptor for the element (which is lazy loading using
		 * <code>configuration</code>)
		 */
		private ImageDescriptor imageDesc;

		private String defaultColumn;
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
			providers = new HashMap<String, IElementLabelProvider>();
			modifiers = new HashMap<String, IElementModifier>();
			defaultColumn = configuration.getAttribute("defaultColumn"); //$NON-NLS-1$
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
			IContributor contributor = configuration.getContributor();
			String iconName = configuration.getAttribute("icon"); //$NON-NLS-1$
			ImageDescriptor desc = EventBImage.getImageDescriptor(contributor
					.getName(), iconName);
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
			return getLabelAtColumn("name", obj); //$NON-NLS-1$
		}

		/**
		 * Getting the priority for an object
		 * <p>
		 * 
		 * @return the priority corresponding to the input object
		 */
		public int getPriority() {
			String priorityString = configuration.getAttribute("priority"); //$NON-NLS-1$
			try {
				return Integer.parseInt(priorityString);
			} catch (NumberFormatException e) {
				String message = "Priority must be a natural number, elements of type "   //$NON-NLS-1$
						+ configuration.getAttribute("type")   //$NON-NLS-1$
						+ " will have default priority " + DEFAULT_PRIORITY; //$NON-NLS-1$
				EventBUIExceptionHandler.handleException(e, message,
						UserAwareness.IGNORE, DEBUG ? DEBUG_PREFIX : null);
				return DEFAULT_PRIORITY;
			}
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
		public String getLabelAtColumn(String columnID, Object obj) {
			IElementLabelProvider provider = providers.get(columnID);
			if (provider != null) {
				try {
					return provider.getLabel(obj);
				} catch (RodinDBException e) {
					return DEFAULT_LABEL;
				}
			}

			IConfigurationElement[] columns = configuration.getChildren();
			for (IConfigurationElement column : columns) {
				String id = column.getAttribute("id"); //$NON-NLS-1$
				if (id.equals(columnID)) {
					try {
						provider = (IElementLabelProvider) column
								.createExecutableExtension("labelProvider"); //$NON-NLS-1$
					} catch (CoreException e) {
						String message = "Cannot instantiate the label provider class "   //$NON-NLS-1$
								+ configuration.getAttribute("labelProvider") //$NON-NLS-1$
								+ ", the label for column " //$NON-NLS-1$
								+ id
								+ " is set to have the default value "   //$NON-NLS-1$
								+ DEFAULT_LABEL;
						EventBUIExceptionHandler.handleException(e, message,
								UserAwareness.IGNORE, DEBUG ? DEBUG_PREFIX : null);
						return DEFAULT_LABEL;
					}
					providers.put(columnID, provider);
					try {
						return provider.getLabel(obj);
					} catch (RodinDBException e) {
						return DEFAULT_LABEL;
					}
				}
			}
			return DEFAULT_LABEL;
		}

		public boolean isNotSelectable(String columnID) {
			IElementModifier modifier = modifiers.get(columnID);
			if (modifier != null) {
				if (modifier instanceof NullModifier)
					return true;
				else
					return false;
			}

			IConfigurationElement[] columns = configuration.getChildren();
			for (IConfigurationElement column : columns) {
				String id = column.getAttribute("id"); //$NON-NLS-1$
				if (id.equals(columnID)) {
					String modifierClass = column.getAttribute("modifier"); //$NON-NLS-1$
					if (modifierClass != null) {
						try {
							modifier = (IElementModifier) column
									.createExecutableExtension("modifier"); //$NON-NLS-1$
						} catch (CoreException e) {
							String message = "Cannot instantiate the modifier class "   //$NON-NLS-1$
									+ modifierClass
									+ ", the column "   //$NON-NLS-1$
									+ id
									+ " is set to have default edit-able attribute "   //$NON-NLS-1$
									+ DEFAULT_EDITABLE;
							EventBUIExceptionHandler.handleException(e,
									message, UserAwareness.IGNORE,
									DEBUG ? DEBUG_PREFIX : null);
							return DEFAULT_EDITABLE;
						}
						modifiers.put(columnID, modifier);
						return false;
					}
				}
			}
			return DEFAULT_EDITABLE;
		}

		public void modify(IRodinElement element, String columnID, String text)
				throws RodinDBException {
			IElementModifier modifier = modifiers.get(columnID);
			if (modifier != null) {
				if (modifier instanceof NullModifier)
					return;
				else
					modifier.modify(element, text);
			}

			IConfigurationElement[] columns = configuration.getChildren();
			for (IConfigurationElement column : columns) {
				String id = column.getAttribute("id"); //$NON-NLS-1$
				if (id.equals(columnID)) {
					try {
						modifier = (IElementModifier) column
								.createExecutableExtension("modifier");   //$NON-NLS-1$
					} catch (CoreException e) {
						String message = "Cannot instantiate the modifier class "   //$NON-NLS-1$
								+ modifier
								+ ", the column "   //$NON-NLS-1$
								+ id
								+ " is set to have no modifier";   //$NON-NLS-1$
						EventBUIExceptionHandler.handleException(e, message,
								UserAwareness.IGNORE, DEBUG ? DEBUG_PREFIX
										: null);
						return;
					}
					modifiers.put(columnID, modifier);
					modifier.modify(element, text);
				}
			}
		}

		public String getDefaultColumn(IRodinElement element) {
			return defaultColumn;
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
	public static IElementUIRegistry getDefault() {
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

		registry = new HashMap<IElementType<?>, ElementUIInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(ELEMENTUI_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement configuration : configurations) {
			String type = configuration.getAttribute("type"); //$NON-NLS-1$

			IElementType<?> elementType;
			try {
				elementType = RodinCore.getElementType(type);
			} catch (IllegalArgumentException e) {
				String message = "Illegal element type " + type   //$NON-NLS-1$
						+ ", ignore this configuration";   //$NON-NLS-1$
				EventBUIExceptionHandler.handleException(e, message,
						UserAwareness.IGNORE, DEBUG ? DEBUG_PREFIX : null);
				continue;
			}
			ElementUIInfo oldInfo = registry.put(elementType,
					new ElementUIInfo(configuration));
			if (oldInfo != null) {
				registry.put(elementType, oldInfo);
				if (DEBUG) {
					UIUtils.printDebugMessage(DEBUG_PREFIX,
									"Configuration is already exists for element of type "   //$NON-NLS-1$
									+ type + ", ignore configuration with id "   //$NON-NLS-1$
									+ configuration.getAttribute("id")); //$NON-NLS-1$
				}
			} else {
				if (DEBUG) {
					UIUtils.printDebugMessage(DEBUG_PREFIX,
							"Registered element UI for element type " + type);   //$NON-NLS-1$
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#getImageDescriptor(org.rodinp.core.IElementType)
	 */
	public synchronized ImageDescriptor getImageDescriptor(IElementType<?> type) {
		if (registry == null)
			loadRegistry();

		ElementUIInfo info = registry.get(type);
		if (info != null)
			return info.getImageDescriptor();
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#getLabel(java.lang.Object)
	 */
	public synchronized String getLabel(Object obj) {
		if (registry == null)
			loadRegistry();

		if (obj instanceof IRodinElement) {
			IElementType<?> elementType = ((IRodinElement) obj)
								.getElementType();
			ElementUIInfo info = registry.get(elementType);
			if (info != null)
				return info.getLabel(obj);
		}

		return DEFAULT_LABEL;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#getLabelAtColumn(java.lang.String, java.lang.Object)
	 */
	public synchronized String getLabelAtColumn(String columnID, Object obj) {
		if (registry == null)
			loadRegistry();

		if (obj instanceof IRodinElement) {
			ElementUIInfo info = registry.get(((IRodinElement) obj)
					.getElementType());
			if (info != null)
				return info.getLabelAtColumn(columnID, obj);
		}

		return DEFAULT_LABEL;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#getPriority(java.lang.Object)
	 */
	public synchronized int getPriority(Object obj) {
		if (registry == null)
			loadRegistry();

		if (obj instanceof IRodinElement) {
			ElementUIInfo info = registry.get(((IRodinElement) obj)
					.getElementType());
			if (info != null)
				return info.getPriority();
		}

		return DEFAULT_PRIORITY;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#isNotSelectable(java.lang.Object, java.lang.String)
	 */
	public synchronized boolean isNotSelectable(Object obj, String columnID) {
		if (registry == null)
			loadRegistry();

		if (obj instanceof IRodinElement) {
			ElementUIInfo info = registry.get(((IRodinElement) obj)
					.getElementType());
			if (info != null)
				return info.isNotSelectable(columnID);
		}

		return DEFAULT_EDITABLE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#modify(org.rodinp.core.IRodinElement, java.lang.String, java.lang.String)
	 */
	public synchronized void modify(IRodinElement element, String columnID,
			String text) throws RodinDBException {
		if (registry == null)
			loadRegistry();

		ElementUIInfo info = registry.get(element.getElementType());
		if (info != null)
			info.modify(element, columnID, text);

	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.IElementUIRegistry#getDefaultColumn(org.rodinp.core.IRodinElement)
	 */
	public synchronized String getDefaultColumn(IRodinElement element) {
		if (registry == null)
			loadRegistry();

		ElementUIInfo info = registry.get(element.getElementType());
		if (info != null)
			return info.getDefaultColumn(element);

		return null;
	}
}
