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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public final class Attribute {
	private final IAttributeType type;
	private final Object value;
	private final int hashCode;

	public Attribute(IAttributeType type, Object value) {
		this.type = type;
		this.value = value;
		hashCode = (type.hashCode() * 17 + value.hashCode()) * 17;
	}

	public IAttributeType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute att = (Attribute) obj;
			return getType().equals(att.getType())
					&& getValue().equals(att.getValue());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return value + "[" + type.getName() + "]";
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public static Attribute valueOf(IInternalElement element,
			IAttributeType type) throws RodinDBException {
		assert element.hasAttribute(type);
		
		if (type instanceof IAttributeType.Long) {
			return new Attribute(type, element.getAttributeValue((IAttributeType.Long) type));
		} else if (type instanceof IAttributeType.String) {
			return new Attribute(type, element.getAttributeValue((IAttributeType.String) type));
		} else if (type instanceof IAttributeType.Boolean) {
			return new Attribute(type, element.getAttributeValue((IAttributeType.Boolean) type));
		} else if (type instanceof IAttributeType.Handle) {
			return new Attribute(type, element.getAttributeValue((IAttributeType.Handle) type));
		} else if (type instanceof IAttributeType.Integer) {
			return new Attribute(type, element.getAttributeValue((IAttributeType.Integer) type));
		}
		
		assert false;
		return null;
	}

}
