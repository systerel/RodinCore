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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.elementSpecs.ElementSpecRegistry;
import org.eventb.internal.ui.elementSpecs.IAttributeRelationship;
import org.eventb.internal.ui.elementSpecs.IElementSpecRegistry;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is abstract class for attribute relationship UI spec. registry.
 *         By sub-class this class, clients can "plug-in" different extension
 *         point instead of a default one (only used for testing purpose).
 *         </p>
 *         <p>
 *         Client should use the corresponding interface
 *         {@link IAttributeRelUISpecRegistry} instead.
 *         </p>
 */
public abstract class AbstractAttributeRelUISpecRegistry implements
		IAttributeRelUISpecRegistry {

	// The element spec registry, this should point to the registry from package
	// org.rodinp.core. Currently, it is implemented by a "fake" registry.
	protected IElementSpecRegistry specRegistry;

	// Map from element type to list of attribute IDs
	Map<IElementType<?>, List<IAttributeRelationship>> attributeRelationships;

	// Map from attribute ID to attribute UI information
	Map<IAttributeRelationship, AttributeRelationshipInfo> attributeRelInfos;

	// ID of the element/attribute relationship ui spec extension point
	private final static String ELEMENT_UI_SPECS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".elementUISpecs";

	// The extension point for attribute relationship UI Spec.
	protected IExtensionPoint extensionPoint;

	
	/**
	 * Constructor: Setting the element spec. registry and extension point for
	 * the attribute relationship. Sub class can re-initialise the registry and
	 * extension point differently.
	 */
	protected AbstractAttributeRelUISpecRegistry() {
		// Setting the 
		extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
				ELEMENT_UI_SPECS_ID);
		specRegistry = ElementSpecRegistry.getDefault();
	}

	/**
	 * @author htson
	 *         <p>
	 *         This utility class is used to stored the information about an
	 *         attribute relationship.
	 *         </p>
	 */
	private class AttributeRelationshipInfo {
		// The configuration element.
		IConfigurationElement config;

		// The priority of the attribute.
		private int priority;

		// Pre-defined default priority.
		private static final int DEFAULT_PRIORITY = 10000;

		// The factory for creating default attribute value.
		private IAttributeFactory factory;
		
		/**
		 * Constructor.
		 * 
		 * @param config
		 *            a configuration element
		 */
		public AttributeRelationshipInfo(IConfigurationElement config) {
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
					EventBEditorUtils.debug("Illegal priority " + priorityValue
							+ ", using default instead,"
							+ " for editor page extension");
				return DEFAULT_PRIORITY;
			}
		}

		/**
		 * Return the priority of the attribute.
		 * 
		 * @return the priority of the attribute.
		 */
		public int getPriority() {
			return this.priority;
		}

		/**
		 * Create the default value for this attribute of the given element.
		 * 
		 * @param editor
		 *            an Event-B Editor
		 * @param element
		 *            the element whose attribute is in concern
		 * @param monitor
		 *            a progress monitor
		 * @throws RodinDBException
		 *             if some problems occurred.
		 */
		public void createDefaultAttribute(IEventBEditor<?> editor,
				IInternalElement element, IProgressMonitor monitor)
				throws RodinDBException {
			if (getDefaultPrefix() == null)
				return;
			
			if (factory == null)
				factory = loadFactory();

			factory.setDefaultValue(editor, element, monitor);
		}

		/*
		 * Utility method for loading the factory class.
		 * 
		 * @return the attribute factory class as declared in the extension
		 *         point. Return The dummy factory class if some problems
		 *         occurred.
		 */
		private IAttributeFactory loadFactory() {
			try {
				Object obj = config
						.createExecutableExtension("factory");
				if (obj instanceof IAttributeFactory)
					return (IAttributeFactory) obj;
			} catch (CoreException e) {
				if (EventBEditorUtils.DEBUG)
					e.printStackTrace();
			}
			return new DummyAttributeFactory();
		}

		/*
		 * @author htson
		 *         <p>
		 *         Utility dummy class implement {@link IAttributeFactory} which
		 *         does nothing
		 *         </p>
		 */
		class DummyAttributeFactory implements IAttributeFactory {

			public String[] getPossibleValues(IAttributedElement element,
					IProgressMonitor monitor) throws RodinDBException {
				// Not applicable to dummy factory
				return null;
			}

			public String getValue(IAttributedElement element,
					IProgressMonitor monitor) throws RodinDBException {
				// Return the empty string.
				return "";
			}

			public void removeAttribute(IAttributedElement element,
					IProgressMonitor monitor) throws RodinDBException {
				// Do nothing
			}

			public void setDefaultValue(IEventBEditor<?> editor,
					IAttributedElement element, IProgressMonitor monitor)
					throws RodinDBException {
				// Do nothing
			}

			public void setValue(IAttributedElement element, String value,
					IProgressMonitor monitor) throws RodinDBException {
				// Do nothing
			}

		}

		/**
		 * Get the edit composite corresponding to the attribute.
		 * 
		 * @return The edit composite with the corresponding attributes. Return
		 *         <code>null</code> if some problems occurred.
		 */
		public IEditComposite getEditComposite() {
			if (factory == null)
				factory = loadFactory();
			
			String prefix = config.getAttribute("prefix");
			String postfix = config.getAttribute("postfix");
			
			boolean fillHorizontal = config.getAttribute(
				"horizontalExpand").equalsIgnoreCase("true");
			IAttributeRelationship attributeRelationship = specRegistry
					.getAttributeRelationship(config.getAttribute("id"));
			
			IAttributeType attributeType = attributeRelationship
					.getAttributeType();
			IAttributeUISpec uiSpec = new AttributeUISpec(factory,
					attributeType, prefix, postfix, fillHorizontal);
			
			String widget = config.getAttribute("widget");
			IEditComposite editComposite;
			if (widget.equals("text")) {
				editComposite = new TextEditComposite(uiSpec);
			} else if (widget.equals("combo")) {
				editComposite = new CComboEditComposite(uiSpec);
			} else {
				editComposite = new DummyEditComposite(uiSpec);
			}

			return editComposite;
		}

		/*
		 * @author htson
		 *         <p>
		 *         Utility dummy class implement {@link IEditComposite} which
		 *         does nothing
		 *         </p>
		 */
		private class DummyEditComposite extends AbstractEditComposite {

			public DummyEditComposite(IAttributeUISpec uiSpec) {
				super(uiSpec);
			}

			@Override
			public void initialise(boolean refreshMarker) {
				// Do nothing
			}

			public void setUndefinedValue() {
				// Do nothing
			}

			public void edit(int charStart, int charEnd) {
				// Do nothing
			}
			
		}

		/**
		 * Return the default prefix.
		 * 
		 * @return a string for default prefix or <code>null</code> if not
		 *         defined.
		 */
		public String getDefaultPrefix() {
			return config.getAttribute("defaultValue");
		}
	}

	/*
	 * Utility method to load the attribute relationship registry.
	 */
	private synchronized void loadRegistry() {
		if (attributeRelInfos != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		// Initilise the data
		attributeRelInfos = new HashMap<IAttributeRelationship, AttributeRelationshipInfo>();
		attributeRelationships = new HashMap<IElementType<?>, List<IAttributeRelationship>>();

		// Read from the extension point
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			// Only concern with extension element named "attributeRel"
			if (!configuration.getName().equals("attributeRel"))
				continue;

			// Get the id
			String id = configuration.getAttribute("id");

			// Check with the element Spec. Registry to see if the id is valid
			// attribute relationship
			if (specRegistry.isValidAttributeRelationship(id)) {
				AttributeRelationshipInfo info = new AttributeRelationshipInfo(
						configuration);
				IAttributeRelationship attributeRelationship = specRegistry
						.getAttributeRelationship(id);

				// Add the relationship to the associated element type
				addAttributeRelationship(
						attributeRelationship.getElementType(),
						attributeRelationship);
				
				// Associate the information with the attribute relationship 
				attributeRelInfos.put(attributeRelationship, info);
			} else {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils
							.debug("Invalid id for attribute relationship, ignore "
									+ id);
				}
			}
		}
	}

	/*
	 * Utility method to add the attribute relationship to its associated type.
	 * 
	 * @param type
	 *            an element type
	 * @param attributeRelationship
	 *            an attribute relationship
	 */
	private void addAttributeRelationship(IElementType<?> type,
			IAttributeRelationship attributeRelationship) {
		List<IAttributeRelationship> list = attributeRelationships
				.get(type);
		if (list == null) {
			list = new ArrayList<IAttributeRelationship>();
			attributeRelationships.put(type, list);
		}
		list.add(attributeRelationship);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecReigstry#createElement(org.eventb.ui.eventbeditor.IEventBEditor, org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, org.rodinp.core.IInternalElement)
	 */
	public synchronized IInternalElement createElement(
			final IEventBEditor<?> editor, IInternalParent parent,
			final IInternalElementType<?> type, final IInternalElement sibling)
			throws CoreException {
		if (attributeRelationships == null)
			loadRegistry();

		String newName = UIUtils.getFreeChildName(editor, parent, type);
		final IInternalElement newElement = parent.getInternalElement(type,
				newName);
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws RodinDBException {
				newElement.create(sibling, monitor);
				List<IAttributeRelationship> list = attributeRelationships
						.get(type);
				if (list != null) {
					for (IAttributeRelationship rel : list) {
						AttributeRelationshipInfo info = attributeRelInfos
								.get(rel);
						info
								.createDefaultAttribute(editor, newElement,
										monitor);
					}
				}
			}

		}, null);

		return newElement;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecReigstry#getNumberOfAttributes(org.rodinp.core.IElementType)
	 */
	public synchronized int getNumberOfAttributes(IElementType<?> type) {
		if (attributeRelationships == null)
			loadRegistry();

		List<IAttributeRelationship> list = attributeRelationships.get(type);
		if (list == null)
			return 0;
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecReigstry#getEditComposites(org.rodinp.core.IElementType)
	 */
	public synchronized IEditComposite[] getEditComposites(
			IElementType<?> type) {
		if (attributeRelationships == null)
			loadRegistry();

		List<IAttributeRelationship> list = attributeRelationships.get(type);
		if (list == null)
			return new IEditComposite[0];
		IEditComposite[] result = new IEditComposite[list.size()];
		for (int i = 0; i < list.size(); ++i) {
			AttributeRelationshipInfo info = attributeRelInfos.get(list.get(i));
			result[i] = info.getEditComposite();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecRegistry#getDefaultPrefix(java.lang.String)
	 */
	public synchronized String getDefaultPrefix(String attributeID) {
		if (attributeRelationships == null)
			loadRegistry();

		AttributeRelationshipInfo info = attributeRelInfos.get(specRegistry
				.getAttributeRelationship(attributeID));
		if (info != null)
			return info.getDefaultPrefix();

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecRegistry#getType(java.lang.String)
	 */
	public synchronized IInternalElementType<?> getType(String attributeID) {
		if (attributeRelationships == null)
			loadRegistry();

		IAttributeRelationship attributeRelationship = specRegistry
				.getAttributeRelationship(attributeID);
		AttributeRelationshipInfo info = attributeRelInfos
				.get(attributeRelationship);

		if (info != null)
			return attributeRelationship.getElementType();

		return null;
	}

}
