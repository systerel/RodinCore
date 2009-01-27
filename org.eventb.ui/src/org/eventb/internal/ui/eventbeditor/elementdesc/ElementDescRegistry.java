/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.TextDesc.Style;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * This enumeration represent a singleton of IElementDescRegistry.
 * <p>
 * It must not have more than one element.
 */
public class ElementDescRegistry implements IElementDescRegistry {

	private static final String ATTR_AUTONAMING_ELEMENT_TYPE = "elementTypeId";
	private static final String ATTR_AUTONAMING_ATTRIBUTE_TYPE = "attributeDescriptionId";
	private static final String ATTR_AUTONAMING_NAME_PREFIX = "namePrefix";

	private static final String ATTR_ATTRIBUTE_PREFIX = "prefix";
	private static final String ATTR_ATTRIBUTE_SUFFIX = "suffix";
	private static final String ATTR_ATTRIBUTE_EXPANDS = "expandsHorizontally";
	private static final String ATTR_ATTRIBUTE_MATH = "isMath";
	private static final String ATTR_ATTRIBUTE_TYPE = "typeId";
	private static final String ATTR_ATTRIBUTE_ID = "id";

	private static final ElementDescRegistry INSTANCE = new ElementDescRegistry();

	final IElementDesc nullElement = new NullElementDesc();

	public static ElementDescRegistry getInstance() {
		return INSTANCE;
	}

	public IElementDesc getElementDesc(IElementType<?> type) {
		return elementDescs.get(type);
	}

	public IElementDesc getElementDesc(IRodinElement element) {
		return getElementDesc(element.getElementType());
	}

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

	private boolean isLastChild(IElementDesc parent, IElementType<?> child) {
		final IElementType<?>[] types = parent.getChildTypes();
		if (types.length == 0)
			return false;
		return (types[types.length - 1] == child);
	}

	private final int LOWEST_PRIORITY = 1;
	private final int HIGHEST_PRIORITY = 1000;

	public int getPriority(Object object) {
		if (!(object instanceof IRodinElement))
			return HIGHEST_PRIORITY;

		final IRodinElement element = (IRodinElement) object;
		final IElementDesc parentDesc;
		// TODO to change : used in the project and obligation explorer
		if (element.getParent() instanceof IRodinFile) {
			parentDesc = getElementDesc(IRodinProject.ELEMENT_TYPE);
		} else {
			parentDesc = getElementDesc(element.getParent());
		}

		if (parentDesc == null)
			return HIGHEST_PRIORITY;

		int count = LOWEST_PRIORITY;
		for (IElementType<?> type : parentDesc.getChildTypes()) {
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
			throws CoreException {
		String newName = UIUtils.getFreeChildName(root, parent, type);
		final T newElement = parent.getInternalElement(type, newName);
		final IAttributeDesc[] attrDesc = getAttributes(type);
		newElement.create(sibling, null);
		for (IAttributeDesc desc : attrDesc) {
			desc.getManipulation().setDefaultValue(newElement, null);
		}
		return newElement;
	}

	final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private final String defaultAttributeValue = "";
	ElementMap elementDescs;

	private ElementDescRegistry() {
		computeAttributeDesc();
	}

	private final String EDITOR_ITEMS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editorItems";

	private void computeAttributeDesc() {
		final List<IConfigurationElement> elementFromExt = new ArrayList<IConfigurationElement>();
		final AttributeMap attributeDescs = new AttributeMap();
		final ChildRelationMap childRelation = new ChildRelationMap();
		final AutoNamingMap autoNaming = new AutoNamingMap();
		final AttributeRelationMap attributeRelation = new AttributeRelationMap();
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] elements = registry
				.getConfigurationElementsFor(EDITOR_ITEMS_ID);

		for (IConfigurationElement element : elements) {
			if (element.getName().equals("declaration")) {
				addArrayToList(elementFromExt, element.getChildren("element"));
				attributeDescs.putAll(element.getChildren("textAttribute"));
				attributeDescs.putAll(element.getChildren("choiceAttribute"));
			} else if (element.getName().equals("relation")) {
				childRelation.putAll(element.getChildren("childRelation"));
				autoNaming.putAll(element.getChildren("autoNaming"));
				attributeRelation.putAll(element
						.getChildren("attributeRelation"));
			}
		}

		elementDescs = new ElementMap(attributeDescs, attributeRelation,
				childRelation, autoNaming);
		elementDescs.put(elementFromExt);
	}

	private void addArrayToList(List<IConfigurationElement> list,
			IConfigurationElement[] elements) {
		for (IConfigurationElement element : elements) {
			list.add(element);
		}
	}

	abstract class ItemMap {
		/**
		 * Return the value of a string attribute with the given name, or "" is
		 * there is not.
		 * */
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

	class ChildRelationMap extends ItemMap {
		final HashMap<IElementType<?>, ArrayList<IElementType<?>>> map = new HashMap<IElementType<?>, ArrayList<IElementType<?>>>();
		final IElementType<?>[] noChildren = new IElementType<?>[0];

		@Override
		public void put(IConfigurationElement element) {
			final IElementType<?> parent = RodinCore.getElementType(element
					.getAttribute("parentTypeId"));
			ArrayList<IElementType<?>> children = map.get(parent);
			if (children == null) {
				children = new ArrayList<IElementType<?>>();
				map.put(parent, children);
			}
			for (IConfigurationElement child : element.getChildren("childType")) {
				children.add(RodinCore.getElementType(child
						.getAttribute("typeId")));
			}
		}

		public IElementType<?>[] get(IElementType<?> parent) {
			final ArrayList<IElementType<?>> children = map.get(parent);
			if (children == null || children.size() == 0) {
				return noChildren;
			}
			return children.toArray(new IElementType<?>[children.size()]);
		}
	}

	class AutoNamingMap extends ItemMap {
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

	class AttributeRelationMap extends ItemMap {
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

	class AttributeMap extends ItemMap {
		final HashMap<String, AttributeDesc> map = new HashMap<String, AttributeDesc>();

		private boolean getBoolean(IConfigurationElement element, String name) {
			final String value = element.getAttribute(name);
			return "true".equals(value);
		}

		private Style getStyle(IConfigurationElement element) {
			final String value = element.getAttribute("style");
			if (value == null)
				return null;
			return value.equals("simple") ? TextDesc.Style.SINGLE
					: TextDesc.Style.MULTI;
		}

		private IAttributeManipulation getManipulation(
				IConfigurationElement element)
				throws InvalidRegistryObjectException, ClassNotFoundException,
				IllegalArgumentException, SecurityException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			Class<? extends IAttributeManipulation> c = Class.forName(
					element.getAttribute("class")).asSubclass(
					IAttributeManipulation.class);
			return c.getConstructor().newInstance();

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
					desc = new TextDesc(manipulation, prefix, suffix,
							isHorizontalExpand, isMath, style, attrType);
				} else {
					desc = new ComboDesc(manipulation, prefix, suffix,
							isHorizontalExpand, attrType);
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

	class ElementMap extends ItemMap {
		final HashMap<IElementType<?>, ElementDesc> elementMap = new HashMap<IElementType<?>, ElementDesc>();
		final AttributeMap attributeMap;
		final ChildRelationMap childRelationMap;
		final AutoNamingMap autoNamingMap;
		final AttributeRelationMap attributeRelationMap;

		public ElementMap(AttributeMap attributeMap,
				AttributeRelationMap attributeRelationMap,
				ChildRelationMap childRelationMap, AutoNamingMap autoNamingMap) {
			this.attributeMap = attributeMap;
			this.childRelationMap = childRelationMap;
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
			final String imageName = getStringAttribute(element, "imagePath");
			final ImageDescriptor imageDesc = EventBImage.getImageDescriptor(
					element.getContributor().getName(), imageName);
			final int defaultColumn = getDefaultColumn(element);
			final IElementType<?> elementType = getElementType(element);

			final IElementType<?>[] childrenType = childRelationMap
					.get(elementType);

			final List<IAttributeDesc> attributesList = new ArrayList<IAttributeDesc>();
			final List<IAttributeDesc> atColumnList = new ArrayList<IAttributeDesc>();
			getAttributes(elementType, attributesList, atColumnList);
			final IAttributeDesc[] attributeDesc = getArray(attributesList);
			final IAttributeDesc[] atColumn = getArray(atColumnList);

			final IConfigurationElement autoNamingConfig = autoNamingMap
					.get(elementType);
			final String autoNamePrefix = getAutoNamingPrefix(autoNamingConfig);
			final IAttributeDesc autoNameAttribute = getAutoNamingAttribute(autoNamingConfig);

			final ElementDesc elementDesc = new ElementDesc(prefix,
					childrenSuffix, imageDesc, attributeDesc, atColumn,
					childrenType, autoNamePrefix, autoNameAttribute,
					defaultColumn);

			elementMap.put(elementType, elementDesc);
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

		/**
		 * @param element
		 *            an autoNaming configuration element
		 * */
		private String getAutoNamingPrefix(IConfigurationElement element) {
			return getStringAttribute(element, ATTR_AUTONAMING_NAME_PREFIX);
		}

		/**
		 * @param element
		 *            an autoNaming configuration element
		 * */
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
