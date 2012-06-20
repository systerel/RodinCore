/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - replaced childTypes by childRelationships
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.TextDesc.Style;
import org.eventb.internal.ui.eventbeditor.imageprovider.DefaultImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IImplicitChildProvider;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Registry for element descriptors contributed through the
 * <code>org.eventb.ui.editorItems</code> extension point.
 */
public class ElementDescRegistry implements IElementDescRegistry {

	private static final String ATTR_AUTONAMING_ELEMENT_TYPE = "elementTypeId";
	private static final String ATTR_AUTONAMING_ATTRIBUTE_TYPE = "attributeDescriptionId";
	private static final String ATTR_AUTONAMING_NAME_PREFIX = "namePrefix";

	private static final String ATTR_ATTRIBUTE_PREFIX = "prefix";
	private static final String ATTR_ATTRIBUTE_SUFFIX = "suffix";
	private static final String ATTR_ATTRIBUTE_EXPANDS = "expandsHorizontally";
	private static final String ATTR_ATTRIBUTE_MATH = "isMath";
	private static final String ATTR_ATTRIBUTE_REQUIRED = "required";
	private static final String ATTR_ATTRIBUTE_TYPE = "typeId";
	private static final String ATTR_ATTRIBUTE_ID = "id";
	private static final String ATTR_ATTRIBUTE_FOREGROUND_COLOR = "foregroundColor";

	private static final String ATTR_ELEMENT_IMAGE_PROVIDER = "imageProvider";
	private static final String ATTR_ELEMENT_IMAGE_PATH = "imagePath";
	private static final String ATTR_ELEMENT_PRETTYPRINTER = "prettyPrinter";
	private static final String ATTR_ELEMENT_IMPLICIT_CHILD_PROVIDER = "implicitChildProvider";

	static final IElementDesc nullElement = new NullElementDesc();
	static final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private static final String defaultAttributeValue = "";

	private static final int LOWEST_PRIORITY = 1;
	private static final int HIGHEST_PRIORITY = 1000;

	private static final ElementDescRegistry INSTANCE = new ElementDescRegistry();
	private final List<ImplicitChildProviderAssociation> childProviderAssocs = new ArrayList<ImplicitChildProviderAssociation>();

	public static ElementDescRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the descriptor for the given element type. This method never
	 * returns <code>null</code>. If there is no declared descriptor for the
	 * given element type, an instance of {@link NullElementDesc} is returned.
	 * 
	 * @return the descriptor for the given element type
	 */
	@Override
	public IElementDesc getElementDesc(IElementType<?> type) {
		return elementDescs.get(type);
	}

	/**
	 * Returns the descriptor for the given element (based on it element type).
	 * This method never returns <code>null</code>. If there is no declared
	 * descriptor for the given element, an instance of {@link NullElementDesc}
	 * is returned.
	 * 
	 * @return the descriptor for the given element
	 */
	@Override
	public IElementDesc getElementDesc(IRodinElement element) {
		return getElementDesc(element.getElementType());
	}

	@Override
	public String getValueAtColumn(IRodinElement element, Column column) {
		final IElementDesc desc = getElementDesc(element);
		if (desc == null)
			return defaultAttributeValue;
		final IAttributeDesc attrDesc = desc.atColumn(column.getId());
		if (attrDesc == null)
			return defaultAttributeValue;
		try {
			if (!attrDesc.getManipulation().hasValue(element, null))
				return defaultAttributeValue;
			return attrDesc.getManipulation().getValue(element, null);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return defaultAttributeValue;
		}

	}

	private IAttributeDesc[] getAttributes(IElementType<?> type) {
		final IElementDesc desc = getElementDesc(type);
		return desc.getAttributeDescription();
	}

	public IAttributeDesc getAttribute(IElementType<?> type, int pos) {
		final IAttributeDesc[] attrDesc = getAttributes(type);
		if (pos < 0 || attrDesc.length <= pos)
			return null;
		return attrDesc[pos];
	}

	public String getPrefix(IElementType<?> type) {
		return getElementDesc(type).getPrefix();
	}

	public String getChildrenSuffix(IElementType<?> parentType,
			IElementType<?> childType) {
		final IElementDesc parentDesc = getElementDesc(parentType);
		if (!isLastChild(parentDesc, childType))
			return "";
		return parentDesc.getChildrenSuffix();
	}

	public IElementType<?>[] getChildTypes(IElementType<?> type) {
		return getElementDesc(type).getChildTypes();
	}

	public IElementRelationship[] getChildRelationships(IElementType<?> type) {
		return getElementDesc(type).getChildRelationships();
	}

	private boolean isLastChild(IElementDesc parent, IElementType<?> child) {
		final IElementRelationship[] relationship = parent
				.getChildRelationships();
		final int len = relationship.length;
		return len != 0 && relationship[len - 1].getChildType() == child;
	}

	public int getPriority(Object object) {
		if (!(object instanceof IInternalElement))
			return HIGHEST_PRIORITY;

		final IInternalElement element = (IInternalElement) object;
		final IElementDesc parentDesc = getElementDesc(element.getParent());

		int count = LOWEST_PRIORITY;
		for (IElementRelationship type : parentDesc.getChildRelationships()) {
			if (type == element.getElementType()) {
				return count;
			}
			count++;
		}
		return HIGHEST_PRIORITY;
	}

	public <T extends IInternalElement> T createElement(
			final IInternalElement root, IInternalElement parent,
			final IInternalElementType<T> type, final IInternalElement sibling)
			throws RodinDBException {
		final T newElement = parent.createChild(type, sibling, null);
		final IAttributeDesc[] attrDesc = getAttributes(type);
		for (IAttributeDesc desc : attrDesc) {
			desc.getManipulation().setDefaultValue(newElement, null);
		}
		return newElement;
	}

	ElementMap elementDescs;

	private ElementDescRegistry() {
		computeAttributeDesc();
	}

	private final String EDITOR_ITEMS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editorItems";

	private void computeAttributeDesc() {
		final List<IConfigurationElement> elementFromExt = new ArrayList<IConfigurationElement>();
		final AttributeMap attributeDescs = new AttributeMap();
		final Set<IElementRelationship> childRelationships = new HashSet<IElementRelationship>();
		final AutoNamingMap autoNaming = new AutoNamingMap();
		final AttributeRelationMap attributeRelation = new AttributeRelationMap();
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] elements = registry
				.getConfigurationElementsFor(EDITOR_ITEMS_ID);

		for (IConfigurationElement element : elements) {
			final String name = element.getName();
			if (name.equals("element")) {
				elementFromExt.add(element);
			} else if (name.equals("textAttribute")
					|| name.equals("choiceAttribute")
					|| name.equals("toggleAttribute")) {
				attributeDescs.put(element);
			} else if (name.equals("childRelation")) {
				childRelationships.addAll(getElementRelationShips(element));
			} else if (name.equals("autoNaming")) {
				autoNaming.put(element);
			} else if (name.equals("attributeRelation")) {
				attributeRelation.put(element);
			}
		}

		elementDescs = new ElementMap(attributeDescs, attributeRelation,
				childRelationships, autoNaming);
		elementDescs.put(elementFromExt);
	}
	
	
	private Set<IElementRelationship> getElementRelationShips(IConfigurationElement element) {
		final IElementType<?> parent = RodinCore.getElementType(element
				.getAttribute("parentTypeId"));
		Set<IElementRelationship> result = new HashSet<IElementRelationship>();
		for (IConfigurationElement child : element.getChildren("childType")) {
			final String type = child.getAttribute("typeId");
			final IInternalElementType<?> elementType = RodinCore.getInternalElementType(type);
			final int priority = parseInt(child.getAttribute("priority"));
			final IImplicitChildProvider implicitChildProvider = getImplicitChildProvider(child);
			if (parent instanceof IInternalElementType
					&& implicitChildProvider != null) {
				final IInternalElementType<?> parentType = (IInternalElementType<?>) parent;
				childProviderAssocs.add(new ImplicitChildProviderAssociation(
								implicitChildProvider, parentType, elementType));
			}
			result.add(new ElementDescRelationship(parent, elementType, priority, implicitChildProvider));
		}
		return result;
	}
	
	private static IImplicitChildProvider getImplicitChildProvider(
			IConfigurationElement element) {
		if (element.getAttribute(ATTR_ELEMENT_IMPLICIT_CHILD_PROVIDER) != null) {
			Object obj;
			try {
				obj = element.createExecutableExtension(ATTR_ELEMENT_IMPLICIT_CHILD_PROVIDER);
				return (IImplicitChildProvider) obj;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<ImplicitChildProviderAssociation> getChildProviderAssociations() {
		return new ArrayList<ImplicitChildProviderAssociation>(childProviderAssocs);
	}
	
	public static class ImplicitChildProviderAssociation {
		
		private final IImplicitChildProvider provider;
		private final IInternalElementType<?> parent;
		private final IInternalElementType<?> child;

		public ImplicitChildProviderAssociation(
				IImplicitChildProvider provider,
				IInternalElementType<?> parent, IInternalElementType<?> child) {
			this.provider = provider;
			this.parent = parent;
			this.child = child;
		}
		
		public IImplicitChildProvider getProvider() {
			return provider;
		}

		public IInternalElementType<?> getParentType() {
			return parent;
		}
		
		public IInternalElementType<?> getChildType() {
			return child;
		}
		
	}
	
	private static int parseInt(String s) {
		if (s == null) {
			return HIGHEST_PRIORITY;
		}
		try {
			final int priority = Integer.parseInt(s);
			if (priority < LOWEST_PRIORITY) {
				return LOWEST_PRIORITY;
			} else if (HIGHEST_PRIORITY < priority) {
				return HIGHEST_PRIORITY;
			} else {
				return priority;
			}
		} catch (NumberFormatException e) {
			UIUtils.log(e, "Priority attribute is not a number");
			return HIGHEST_PRIORITY;
		}
	}
	
	abstract static class ItemMap {
		/**
		 * Returns the value of a string attribute with the given name, or an
		 * empty string if there is none.
		 */
		protected String getStringAttribute(IConfigurationElement element,
				String name) {
			if (element == null)
				return "";
			final String value = element.getAttribute(name);
			if (value == null)
				return "";
			return value;
		}

		public void putAll(IConfigurationElement[] elements) {
			for (IConfigurationElement element : elements) {
				put(element);
			}
		}

		public abstract void put(IConfigurationElement element);
	}

	static class AutoNamingMap extends ItemMap {
		final HashMap<IElementType<?>, IConfigurationElement> map = new HashMap<IElementType<?>, IConfigurationElement>();

		@Override
		public void put(IConfigurationElement element) {
			final IElementType<?> elementId = RodinCore.getElementType(element
					.getAttribute(ATTR_AUTONAMING_ELEMENT_TYPE));
			map.put(elementId, element);
		}

		public IConfigurationElement get(IElementType<?> elementType) {
			return map.get(elementType);
		}
	}

	static class AttributeRelationMap extends ItemMap {
		final HashMap<IElementType<?>, ArrayList<IConfigurationElement>> map = new HashMap<IElementType<?>, ArrayList<IConfigurationElement>>();
		final IConfigurationElement[] noChildren = new IConfigurationElement[0];

		@Override
		public void put(IConfigurationElement element) {
			final IElementType<?> type = RodinCore.getElementType(element
					.getAttribute("elementTypeId"));
			ArrayList<IConfigurationElement> children = map.get(type);
			if (children == null) {
				children = new ArrayList<IConfigurationElement>();
				map.put(type, children);
			}
			for (IConfigurationElement child : element
					.getChildren("attributeReference")) {
				children.add(child);
			}
		}

		public IConfigurationElement[] get(IElementType<?> elementType) {
			final ArrayList<IConfigurationElement> children = map
					.get(elementType);
			if (children == null || children.size() == 0) {
				return noChildren;
			}
			return children.toArray(new IConfigurationElement[children.size()]);
		}
	}

	static class AttributeMap extends ItemMap {
		final HashMap<String, AttributeDesc> map = new HashMap<String, AttributeDesc>();

		private boolean getBoolean(IConfigurationElement element, String name) {
			final String value = element.getAttribute(name);
			return "true".equals(value);
		}

		private Style getStyle(IConfigurationElement element) {
			final String value = element.getAttribute("style");
			if (value == null)
				return null;
			return value.equals("single") ? TextDesc.Style.SINGLE
					: TextDesc.Style.MULTI;
		}

		private IAttributeManipulation getManipulation(
				IConfigurationElement element) throws CoreException {
			final Object obj = element.createExecutableExtension("class");
			return (IAttributeManipulation) obj;
		}

		private String getForegroundColor(IConfigurationElement element) {
			final String preference = element
					.getAttribute(ATTR_ATTRIBUTE_FOREGROUND_COLOR);
			if (preference == null)
				return PreferenceConstants.P_TEXT_FOREGROUND;
			return preference;
		}
		
		@Override
		public void put(IConfigurationElement element) {
			try {
				final IAttributeManipulation manipulation = getManipulation(element);
				final String prefix = getStringAttribute(element,
						ATTR_ATTRIBUTE_PREFIX);
				final String suffix = getStringAttribute(element,
						ATTR_ATTRIBUTE_SUFFIX);
				final boolean isHorizontalExpand = getBoolean(element,
						ATTR_ATTRIBUTE_EXPANDS);
				final IAttributeType attrType = RodinCore
						.getAttributeType(element
								.getAttribute(ATTR_ATTRIBUTE_TYPE));
				final String name = element.getName();
				final String id = getStringAttribute(element, ATTR_ATTRIBUTE_ID);
				final AttributeDesc desc;
				if (name.equals("textAttribute")) {
					final boolean isMath = getBoolean(element,
							ATTR_ATTRIBUTE_MATH);
					final Style style = getStyle(element);
					final String preference = getForegroundColor(element);
					desc = new TextDesc(manipulation, prefix, suffix,
							isHorizontalExpand, isMath, style, attrType, preference);
				} else if (name.equals("choiceAttribute")) {
					final boolean required = getBoolean(element,
							ATTR_ATTRIBUTE_REQUIRED);
					desc = new ComboDesc(manipulation, prefix, suffix,
							isHorizontalExpand, attrType, required);
				} else if (name.equals("toggleAttribute")) {
					desc = new ToggleDesc(
							(AbstractBooleanManipulation) manipulation,
							attrType);
				} else {
					throw new IllegalStateException("Unknown attribute kind: " + name);
				}
				map.put(id, desc);
			} catch (Exception e) {
				final String message = "Can't instanciate AttributeDesc";
				UIUtils.log(e, message);
			}

		}

		public IAttributeDesc get(String key) {
			AttributeDesc desc = map.get(key);
			if (desc == null)
				return nullAttribute;
			return desc;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (String key : map.keySet()) {
				buffer.append(key);
				buffer.append(":\n  ");
				buffer.append(map.get(key));
				buffer.append('\n');
			}
			return buffer.toString();
		}
	}

	static class ElementMap extends ItemMap {
		final HashMap<IElementType<?>, ElementDesc> elementMap = new HashMap<IElementType<?>, ElementDesc>();
		final AttributeMap attributeMap;
		final Set<IElementRelationship> childRelationships;
		final AutoNamingMap autoNamingMap;
		final AttributeRelationMap attributeRelationMap;

		public ElementMap(AttributeMap attributeMap,
				AttributeRelationMap attributeRelationMap,
				Set<IElementRelationship> childRelationships,
				AutoNamingMap autoNamingMap) {
			this.attributeMap = attributeMap;
			this.childRelationships = childRelationships;
			this.autoNamingMap = autoNamingMap;
			this.attributeRelationMap = attributeRelationMap;
		}

		public void put(List<IConfigurationElement> elements) {
			for (IConfigurationElement configurationElement : elements) {
				put(configurationElement);
			}
		}

		@Override
		public void put(IConfigurationElement element) {
			final String prefix = getStringAttribute(element, "prefix");
			final String childrenSuffix = getStringAttribute(element,
					"childrenSuffix");
			final IImageProvider imgProvider;
			try {
				imgProvider = getImageProvider(element);
			} catch (CoreException e) {
				final String message = "Cannot get the image provider";
				UIUtils.log(e, message);
				return;
			}
			final int defaultColumn = getDefaultColumn(element);
			final IElementType<?> elementType = getElementType(element);

			final IElementRelationship[] childrenRelationships = retrieveElementChildRelationships(elementType);

			final List<IAttributeDesc> attributesList = new ArrayList<IAttributeDesc>();
			final List<IAttributeDesc> atColumnList = new ArrayList<IAttributeDesc>();
			getAttributes(elementType, attributesList, atColumnList);
			final IAttributeDesc[] attributeDesc = getArray(attributesList);
			final IAttributeDesc[] atColumn = getArray(atColumnList);

			final IConfigurationElement autoNamingConfig = autoNamingMap
					.get(elementType);
			final String autoNamePrefix = getAutoNamingPrefix(autoNamingConfig);
			final IAttributeDesc autoNameAttribute = getAutoNamingAttribute(autoNamingConfig);
			final IElementPrettyPrinter prettyPrinter;
			try {
				prettyPrinter = getPrettyPrinter(element);
			} catch (CoreException e) {
				final String message = "Cannot get the pretty printer";
				UIUtils.log(e, message);
				return;
			}
			
			
			final ElementDesc elementDesc = new ElementDesc(prefix,
					childrenSuffix, imgProvider, attributeDesc, atColumn,
					childrenRelationships, autoNamePrefix, autoNameAttribute,
					defaultColumn, prettyPrinter);

			elementMap.put(elementType, elementDesc);
		}
		
		private IElementRelationship[] retrieveElementChildRelationships(
				IElementType<?> elementType) {
			final List<IElementRelationship> l = new LinkedList<IElementRelationship>();
			for (IElementRelationship rel : childRelationships) {
				if (elementType == rel.getParentType()) {
					l.add(rel);
				}
			}
			Collections.sort(l);
			return l.toArray(new IElementRelationship[l.size()]);
		}
		

		private IImageProvider getImageProvider(IConfigurationElement element)
				throws CoreException {
			if (element.getAttribute(ATTR_ELEMENT_IMAGE_PROVIDER) != null) {
				final Object obj = element
						.createExecutableExtension(ATTR_ELEMENT_IMAGE_PROVIDER);
				return (IImageProvider) obj;
			} else {
				final String imageName = getStringAttribute(element,
						ATTR_ELEMENT_IMAGE_PATH);
				final ImageDescriptor imageDesc = EventBImage
						.getImageDescriptor(element.getContributor().getName(),
								imageName);
				return new DefaultImageProvider(imageDesc);
			}
		}
		
		private IElementPrettyPrinter getPrettyPrinter(
				IConfigurationElement element) throws CoreException {
			if (element.getAttribute(ATTR_ELEMENT_PRETTYPRINTER) != null) {
				final Object obj = element
						.createExecutableExtension(ATTR_ELEMENT_PRETTYPRINTER);
				return (IElementPrettyPrinter) obj;
			} else {
				return null;
			}
		}
		
		private IAttributeDesc[] getArray(List<IAttributeDesc> list) {
			return list.toArray(new IAttributeDesc[list.size()]);
		}

		private int getDefaultColumn(IConfigurationElement element) {
			final String value = getStringAttribute(element, "defaultColumn");
			if (value == "")
				return 0;
			return Integer.parseInt(value);
		}

		private IElementType<?> getElementType(IConfigurationElement element) {
			final String value = element.getAttribute("typeId");
			return RodinCore.getElementType(value);
		}

		private void getAttributes(IElementType<?> type,
				List<IAttributeDesc> attributes, List<IAttributeDesc> atColumn) {
			final IConfigurationElement[] children = attributeRelationMap
					.get(type);
			initAtColumn(atColumn, children.length);
			for (IConfigurationElement element : children) {
				IAttributeDesc desc = attributeMap.get(getStringAttribute(
						element, "descriptionId"));
				attributes.add(desc);
				final String column = getStringAttribute(element, "column");
				if (!column.equals("")) {
					atColumn.set(Integer.parseInt(column), desc);
				}
			}
		}

		private void initAtColumn(List<IAttributeDesc> atColumn, int length) {
			for (int i = 0; i < length; i++)
				atColumn.add(nullAttribute);
		}

		private String getAutoNamingPrefix(IConfigurationElement element) {
			return getStringAttribute(element, ATTR_AUTONAMING_NAME_PREFIX);
		}

		private IAttributeDesc getAutoNamingAttribute(
				IConfigurationElement element) {
			if (element == null)
				return nullAttribute;
			return attributeMap.get(getStringAttribute(element,
					ATTR_AUTONAMING_ATTRIBUTE_TYPE));
		}

		public IElementDesc get(IElementType<?> key) {
			final ElementDesc desc = elementMap.get(key);
			if (desc == null)
				return nullElement;
			return desc;
		}
	}
}
