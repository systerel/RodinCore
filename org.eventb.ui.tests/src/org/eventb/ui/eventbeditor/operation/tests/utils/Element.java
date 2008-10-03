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
import java.util.Collection;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public class Element {

	private final ArrayList<Element> children;

	private final ArrayList<Attribute<?,?>> attributes;

	private IInternalElementType<? extends IInternalElement> type;

	public Element(IInternalElementType<? extends IInternalElement> type) {
		this.type = type;
		children = new ArrayList<Element>();
		attributes = new ArrayList<Attribute<?,?>>();
	}

	public IInternalElementType<? extends IInternalElement> getType(){
		return type ;
	}
	
	public void addChildren(Element element) {
		children.add(element);
	}

	public Collection<Element> getChildren() {
		return children;
	}

	public <T_TYPE extends IAttributeType> void addAttribute(
			Attribute<T_TYPE,?> attribute) {
		attributes.add(attribute);
	}

	public Collection<Attribute<?,?>> getAttributes() {
		return attributes;
	}
}
