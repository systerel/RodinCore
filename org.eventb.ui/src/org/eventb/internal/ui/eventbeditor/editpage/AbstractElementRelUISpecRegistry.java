/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.elementSpecs.ElementSpecRegistry;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.elementSpecs.IElementSpecRegistry;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IElementType;

/**
 * @author htson
 *         <p>
 *         This is abstract class for element relationship UI spec. registry.
 *         By sub-class this class, clients can "plug-in" different extension
 *         point instead of a default one (only used for testing purpose).
 *         </p>
 *         <p>
 *         Client should use the corresponding interface
 *         {@link IElementRelUISpecRegistry} instead.
 *         </p>
 */
public abstract class AbstractElementRelUISpecRegistry implements
		IElementRelUISpecRegistry {

	// ID of the element/attribute relationship ui spec extension point
	private final static String ELEMENT_UI_SPECS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".elementUISpecs";
	
	// The extension point for attribute relationship UI Spec.
	protected IExtensionPoint extensionPoint;
	
	// The element spec registry, this should point to the registry from package
	// org.rodinp.core. Currently, it is implemented by a "fake" registry.
	protected IElementSpecRegistry specRegistry;
	
	// Map from parent element type to list of element relationship
	private Map<IElementType<?>, List<IElementRelationship>> elementRelationships;
	
	// Map from attribute ID to element relationship UI information
	private Map<IElementRelationship, ElementRelationshipInfo> elementRelInfos;
	
	/**
	 * Constructor: Setting the element spec. registry and extension point for
	 * the attribute relationship. Sub class can re-initialise the registry and
	 * extension point differently.
	 */
	protected AbstractElementRelUISpecRegistry() {
		extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				ELEMENT_UI_SPECS_ID);
		specRegistry = ElementSpecRegistry.getDefault();
	}

	/**
	 * @author htson
	 *         <p>
	 *         This utility class is used to stored the information about an
	 *         element relationship.
	 *         </p>
	 */
	private class ElementRelationshipInfo {
		// The configuration element.
		IConfigurationElement config;

		// The priority of the element
		private int priority;

		// Pre-defined default priority.
		private static final int DEFAULT_PRIORITY = 10000;

		/**
		 * Constructor.
		 * 
		 * @param config
		 *            a configuration element
		 */
		public ElementRelationshipInfo(IConfigurationElement config) {
			this.config = config;
			priority = readPriority();
		}

		/*
		 * Utility method for reading the priority.
		 * 
		 * @return the priority from the extension point. Return
		 *         {@value #DEFAULT_PRIORITY} if there is some problems
		 *         reading from the extension point.
		 */
		private int readPriority() {
			String priorityValue = config.getAttribute("priority");
			if (priorityValue == null) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils
							.debug("Missing priority attribute (using default),"
									+ " for editor page extension");
				return DEFAULT_PRIORITY;
			}
			try {
				return Integer.parseInt(priorityValue);
			} catch (NumberFormatException e) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Illegal priority "
							+ priorityValue + ", using default instead,"
							+ " for editor page extension");
				return DEFAULT_PRIORITY;
			}
		}

		/**
		 * Get the prefix for element relationship
		 * 
		 * @return the string prefix for the element relationship. Return
		 *         <code>null</code> if it is not defined.
		 */
		public String getPrefix() {
			return config.getAttribute("prefix");
		}

		/**
		 * Get the postfix for element relationship
		 * 
		 * @return the string postfix for the element relationship. Return
		 *         <code>null</code> if it is not defined.
		 */
		public String getPostfix() {
			return config.getAttribute("postfix");
		}

		/**
		 * Get the priority for this element relationship.
		 * 
		 * @return the priority for this element relationship.
		 */
		public int getPriority() {
			return this.priority;
		}

	}

	/*
	 * Utility method to load the element relationship registry.
	 */
	private synchronized void loadRegistry() {
		if (elementRelInfos != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		// Initialise the data
		elementRelInfos = new HashMap<IElementRelationship, ElementRelationshipInfo>();
		elementRelationships = new HashMap<IElementType<?>, List<IElementRelationship>>();
		
		// Read from the extension point
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			// Only concern with extension element named "elementRel"
			if (!configuration.getName().equals("elementRel"))
				continue;

			// Get the id
			String id = configuration.getAttribute("id");
			
			// Check with the element Spec. Registry to see if the id is valid
			// element relationship
			if (specRegistry.isValidElementRelationship(id)) {
				ElementRelationshipInfo info = new ElementRelationshipInfo(
						configuration);
				IElementRelationship elementRelationship = specRegistry
						.getElementRelationship(id);

				// Add the relationship to the associated element type
				addElementRelationship(elementRelationship.getParentType(),
						elementRelationship);
				
				// Associate the information with the attribute relationship 
				elementRelInfos.put(elementRelationship, info);
			}
			else {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils
							.debug("Invalid id for element relationship, ignore "
									+ id);
				}
			}
		}
		
		// Sorting the relationships according to priority
		sortElementRelationships();
	}
	
	/*
	 * Utility method to add the element relationship to its associated type.
	 * 
	 * @param type
	 *            an element type
	 * @param elementRelationship
	 *            an element relationship
	 */
	private void addElementRelationship(IElementType<?> parentType,
			IElementRelationship elementRelationship) {
		List<IElementRelationship> list = elementRelationships.get(parentType);
		if (list == null) {
			list = new ArrayList<IElementRelationship>();
			elementRelationships.put(parentType, list);
		}
		list.add(elementRelationship);
	}

	/*
	 * Utility method for sorting the element relationships according to
	 * priority.
	 */
	private void sortElementRelationships() {
		assert elementRelationships != null;
		
		for (IElementType<?> type : elementRelationships.keySet()) {
			List<IElementRelationship> list = elementRelationships.get(type);
			boolean sorted = false;
			int size = list.size();

			// Repeat until the list is sorted
			while (!sorted) {
				sorted = true;
				for (int i = 0; i < size - 1; i++) {
					IElementRelationship curr = list.get(i);
					IElementRelationship next = list.get(i + 1);
					ElementRelationshipInfo currInfo = elementRelInfos.get(curr);
					ElementRelationshipInfo nextInfo = elementRelInfos.get(next);
					
					// If out of order then swap the elements.
					if (currInfo.getPriority() > nextInfo.getPriority()) {
						list.set(i, next);
						list.set(i + 1, curr);
						sorted = false;
					}
				}
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IElementRelUISpecRegistry#getElementRelationships(org.rodinp.core.IElementType)
	 */
	public synchronized List<IElementRelationship> getElementRelationships(
			IElementType<?> parentType) {
		if (elementRelationships == null)
			loadRegistry();

		List<IElementRelationship> list = elementRelationships.get(parentType);
		if (list == null)
			return new ArrayList<IElementRelationship>();
		
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IElementRelUISpecRegistry#getPrefix(org.eventb.internal.ui.elementSpecs.IElementRelationship)
	 */
	public String getPrefix(IElementRelationship rel) {
		if (elementRelInfos == null)
			loadRegistry();

		ElementRelationshipInfo elementRelationshipInfo = elementRelInfos
				.get(rel);
		return elementRelationshipInfo.getPrefix();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IElementRelUISpecRegistry#getPostfix(org.eventb.internal.ui.elementSpecs.IElementRelationship)
	 */
	public String getPostfix(IElementRelationship rel) {
		if (elementRelInfos == null)
			loadRegistry();

		ElementRelationshipInfo elementRelationshipInfo = elementRelInfos
				.get(rel);
		return elementRelationshipInfo.getPostfix();
	}


}
