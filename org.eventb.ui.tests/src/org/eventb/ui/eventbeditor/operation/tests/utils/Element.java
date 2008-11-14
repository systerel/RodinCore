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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public class Element {

	private final Set<Element> children;

	private final Set<Attribute<?, ?>> attributes;

	private IInternalElementType<? extends IInternalElement> type;

	private Element sibling;

	private static boolean testSibling = false;

	/**
	 * Class that represents an IInternalElement.
	 */
	public Element(IInternalElementType<? extends IInternalElement> type) {
		this.type = type;
		children = new HashSet<Element>();
		attributes = new HashSet<Attribute<?, ?>>();
		sibling = null;
	}

	public IInternalElementType<? extends IInternalElement> getType() {
		return type;
	}

	public void addChildren(Element element, Element nextSibling) {
		assert element != null;
		assert element != sibling;

		for (Element pre : children) {
			if (pre.sibling == nextSibling) {
				pre.sibling = element;
				break;
			}
		}
		children.add(element);
		element.sibling = nextSibling;
	}

	public Collection<Element> getChildren() {
		return new HashSet<Element>(children);
	}

	public <T_TYPE extends IAttributeType> void addAttribute(
			Attribute<T_TYPE, ?> attribute) {
		attributes.add(attribute);
	}

	public Set<Attribute<?, ?>> getAttributes() {
		return new HashSet<Attribute<?, ?>>(attributes);
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
		if (testSibling(this, element))
			return false;
		return children.equals(element.getChildren());
	}

	private boolean testSibling(Element element1, Element element2) {
		if (!testSibling)
			return false;
		else
			return (element1.sibling == null && element2.sibling != null)
					|| (element1.sibling != null && !element1.sibling
							.equals(element2.sibling));
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
		boolean first = true;
		for (Object o : attributes.toArray()) {
			if (!first)
				buffer.append(", ");
			first = false;
			buffer.append(o.toString());
		}
		buffer.append(")\n");
		for (Object o : children.toArray()) {
			buffer.append(" - ");
			buffer.append(o.toString());
			buffer.append("\n");
		}

		return buffer.toString();
	}

	public static void setTestSibling(boolean b) {
		testSibling = b;
	}
}
