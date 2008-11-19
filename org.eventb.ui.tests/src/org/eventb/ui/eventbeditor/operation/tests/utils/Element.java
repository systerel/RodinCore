/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.operation.tests.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class Element {

	private final List<Element> children;

	private final Set<Attribute> attributes;

	private IInternalElementType<? extends IInternalElement> type;

	private Element sibling;

	/**
	 * Class that represents an IInternalElement.
	 */
	public Element(IInternalElementType<? extends IInternalElement> type) {
		this.type = type;
		children = new ArrayList<Element>();
		attributes = new HashSet<Attribute>();
		sibling = null;
	}

	public IInternalElementType<? extends IInternalElement> getType() {
		return type;
	}

	public void addChild(Element element, Element nextSibling) {
		assert element != null;
		assert element != sibling;
		assert nextSibling == null || children.contains(nextSibling);

		if (nextSibling == null) {
			children.add(element);
		} else {
			int index = children.indexOf(sibling);
			children.add(index, element);
		}
	}

	public List<Element> getChildren() {
		return children;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public Set<Attribute> getAttributes() {
		return new HashSet<Attribute>(attributes);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Element))
			return false;
		final Element element = (Element) obj;
		if (getType() != element.getType())
			return false;
		if (!attributes.equals(element.getAttributes()))
			return false;
		return children.equals(element.getChildren());
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(type.getName());
		buffer.append("(");
		String sep = "";
		for (Object o : attributes) {
			buffer.append(sep);
			sep = ", ";
			buffer.append(o.toString());
		}
		buffer.append(")");

		sep = "\n - ";
		for (Object o : children.toArray()) {
			buffer.append(sep);
			buffer.append(o.toString());
		}

		return buffer.toString();
	}

	private static Object getAttributeValue(IInternalElement element,
			IAttributeType type) throws RodinDBException {
		if (!element.hasAttribute(type)) {
			return null;
		}
		if (type instanceof IAttributeType.Long) {
			return element.getAttributeValue((IAttributeType.Long) type);
		} else if (type instanceof IAttributeType.String) {
			return element.getAttributeValue((IAttributeType.String) type);
		} else if (type instanceof IAttributeType.Boolean) {
			return element.getAttributeValue((IAttributeType.Boolean) type);
		} else if (type instanceof IAttributeType.Handle) {
			return element.getAttributeValue((IAttributeType.Handle) type);
		} else if (type instanceof IAttributeType.Integer) {
			return element.getAttributeValue((IAttributeType.Integer) type);
		} else {
			return null;
		}
	}

	private static void addAttributes(IInternalElement root, Element element)
			throws RodinDBException {
		IAttributeType[] types = root.getAttributeTypes();
		for (IAttributeType type : types)
			element.addAttribute(new Attribute(type, getAttributeValue(root,
					type)));
	}

	public static Element valueOf(IInternalElement root)
			throws RodinDBException {
		final Element result = new Element(root.getElementType());
		addAttributes(root, result);

		for (IRodinElement children : root.getChildren())
			result.addChild(valueOf((IInternalElement) children), null);
		return result;
	}

}
