/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
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

import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class Element {

	private final List<Element> children;

	private final Set<IAttributeValue> attributes;

	private IInternalElementType<?> type;

	/**
	 * Class that represents an IInternalElement.
	 */
	public Element(IInternalElementType<?> type) {
		this.type = type;
		children = new ArrayList<Element>();
		attributes = new HashSet<IAttributeValue>();
	}

	public IInternalElementType<?> getType() {
		return type;
	}

	public void addChild(Element element, Element nextSibling) {
		assert element != null;

		if (nextSibling == null) {
			children.add(element);
		} else {
			int index = children.indexOf(nextSibling);
			assert 0 <= index;
			children.add(index, element);
		}
	}

	public List<Element> getChildren() {
		return new ArrayList<Element>(children);
	}

	public void addAttribute(IAttributeValue attribute) {
		attributes.add(attribute);
	}

	public Set<IAttributeValue> getAttributes() {
		return new HashSet<IAttributeValue>(attributes);
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
			buffer.append(o);
		}
		buffer.append(")");

		sep = "\n - ";
		for (Object o : children.toArray()) {
			buffer.append(sep);
			buffer.append(o);
		}

		return buffer.toString();
	}

	public static Element valueOf(IInternalElement ie) throws RodinDBException {
		final Element result = new Element(ie.getElementType());
		addAttributes(ie, result);
		addChildren(ie, result);
		return result;
	}

	private static void addAttributes(IInternalElement ie, Element element)
			throws RodinDBException {
		for (IAttributeValue value: ie.getAttributeValues()) {
			element.addAttribute(value);
		}
	}

	private static void addChildren(IInternalElement element, Element result)
			throws RodinDBException {
		for (IRodinElement child : element.getChildren())
			result.addChild(valueOf((IInternalElement) child), null);
	}

}
