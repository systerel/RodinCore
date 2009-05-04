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
package org.rodinp.internal.core;

import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IRodinElement;

/**
 * Implementation of attribute values.
 * 
 * @author Laurent Voisin
 */
public abstract class AttributeValue<T extends AttributeType<V>, V> implements
		IAttributeValue {

	public static class Boolean extends
			AttributeValue<AttributeType.Boolean, java.lang.Boolean> implements
			IAttributeValue.Boolean {

		public Boolean(AttributeType.Boolean type, boolean value) {
			super(type, value);
		}

	}

	public static class Handle extends
			AttributeValue<AttributeType.Handle, IRodinElement> implements
			IAttributeValue.Handle {

		public Handle(AttributeType.Handle type, IRodinElement value) {
			super(type, value);
		}

	}

	public static class Integer extends
			AttributeValue<AttributeType.Integer, java.lang.Integer> implements
			IAttributeValue.Integer {

		public Integer(AttributeType.Integer type, int value) {
			super(type, value);
		}

	}

	public static class Long extends
			AttributeValue<AttributeType.Long, java.lang.Long> implements
			IAttributeValue.Long {

		public Long(AttributeType.Long type, long value) {
			super(type, value);
		}

	}

	public static class String extends
			AttributeValue<AttributeType.String, java.lang.String> implements
			IAttributeValue.String {

		public String(AttributeType.String type, java.lang.String value) {
			super(type, value);
		}

	}

	private final T type;

	protected final V value;

	public AttributeValue(T type, V value) {
		this.type = type;
		this.value = value;
	}

	public final T getType() {
		return type;
	}

	public final java.lang.String getId() {
		return type.getId();
	}

	public final V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		result = prime * result + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final AttributeValue<?, ?> other = (AttributeValue<?, ?>) obj;
		if (this.type != other.type)
			return false;
		return this.value.equals(other.value);
	}

	public final java.lang.String getRawValue() {
		return type.toString(value);
	}

}
