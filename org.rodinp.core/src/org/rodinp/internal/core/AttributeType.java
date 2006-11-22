/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Util;

/**
 * Implementation of attribute types.
 * 
 * @author Laurent Voisin
 */
public abstract class AttributeType implements IAttributeType {

	private static final java.lang.String KIND_BOOLEAN = "boolean";

	private static final java.lang.String KIND_HANDLE = "handle";

	private static final java.lang.String KIND_INTEGER = "integer";

	private static final java.lang.String KIND_LONG = "long";

	private static final java.lang.String KIND_STRING = "string";

	private static class Boolean extends AttributeType implements
			IAttributeType.Boolean {

		protected Boolean(java.lang.String id, java.lang.String name) {
			super(id, name);
		}

		@Override
		public boolean getBoolValue(java.lang.String rawValue) {
			return java.lang.Boolean.valueOf(rawValue);
		}

		@Override
		public java.lang.String getKind() {
			return KIND_BOOLEAN;
		}

		@Override
		public java.lang.String toString(boolean value) throws RodinDBException {
			return java.lang.Boolean.toString(value);
		}

	}

	private static class Handle extends AttributeType implements
			IAttributeType.Handle {

		protected Handle(java.lang.String id, java.lang.String name) {
			super(id, name);
		}

		@Override
		public IRodinElement getHandleValue(java.lang.String rawValue)
				throws RodinDBException {
			IRodinElement result = RodinCore.valueOf(rawValue);
			if (result == null) {
				Util.log(null, "Can't parse handle value for attribute " + id);
				throw newInvalidValueException();
			}
			return result;
		}

		@Override
		public java.lang.String getKind() {
			return KIND_HANDLE;
		}

		@Override
		public java.lang.String toString(IRodinElement value)
				throws RodinDBException {
			return value.getHandleIdentifier();
		}

	}

	private static class Integer extends AttributeType implements
			IAttributeType.Integer {

		protected Integer(java.lang.String id, java.lang.String name) {
			super(id, name);
		}

		@Override
		public int getIntValue(java.lang.String rawValue)
				throws RodinDBException {
			try {
				return java.lang.Integer.valueOf(rawValue);
			} catch (NumberFormatException e) {
				throw newInvalidValueException();
			}
		}

		@Override
		public java.lang.String getKind() {
			return KIND_INTEGER;
		}

		@Override
		public java.lang.String toString(int value) throws RodinDBException {
			return java.lang.Integer.toString(value);
		}

	}

	private static class Long extends AttributeType implements
			IAttributeType.Long {

		protected Long(java.lang.String id, java.lang.String name) {
			super(id, name);
		}

		@Override
		public java.lang.String getKind() {
			return KIND_LONG;
		}

		@Override
		public long getLongValue(java.lang.String rawValue)
				throws RodinDBException {
			try {
				return java.lang.Long.valueOf(rawValue);
			} catch (NumberFormatException e) {
				throw newInvalidValueException();
			}
		}

		@Override
		public java.lang.String toString(long value) throws RodinDBException {
			return java.lang.Long.toString(value);
		}

	}

	private static class String extends AttributeType implements
			IAttributeType.String {

		protected String(java.lang.String id, java.lang.String name) {
			super(id, name);
		}

		@Override
		public java.lang.String getKind() {
			return KIND_STRING;
		}

		@Override
		public java.lang.String getStringValue(java.lang.String rawValue) {
			return rawValue;
		}

		@Override
		public java.lang.String toString(java.lang.String value)
				throws RodinDBException {
			return value;
		}

	}

	public static AttributeType valueOf(IConfigurationElement ice) {
		final java.lang.String nameSpace = ice.getNamespaceIdentifier();
		final java.lang.String id = nameSpace + "." + ice.getAttribute("id");
		final java.lang.String name = ice.getAttribute("name");
		final java.lang.String kind = ice.getAttribute("kind");
		if (KIND_BOOLEAN.equals(kind)) {
			return new Boolean(id, name);
		}
		if (KIND_HANDLE.equals(kind)) {
			return new Handle(id, name);
		}
		if (KIND_INTEGER.equals(kind)) {
			return new Integer(id, name);
		}
		if (KIND_LONG.equals(kind)) {
			return new Long(id, name);
		}
		if (KIND_STRING.equals(kind)) {
			return new String(id, name);
		}
		Util.log(null, "Unknown attribute kind " + kind
				+ " when parsing configuration element " + id);
		return null;
	}

	protected final java.lang.String id;

	protected final java.lang.String name;

	protected AttributeType(java.lang.String id, java.lang.String name) {
		this.id = id;
		this.name = name;
	}

	public boolean getBoolValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	public IRodinElement getHandleValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	/**
	 * @return Returns the id.
	 */
	public final java.lang.String getId() {
		return id;
	}

	public int getIntValue(java.lang.String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	public abstract java.lang.String getKind();

	public long getLongValue(java.lang.String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	/**
	 * @return Returns the name.
	 */
	public final java.lang.String getName() {
		return name;
	}

	public java.lang.String getStringValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	protected RodinDBException newInvalidValueException() {
		return new RodinDBException(new RodinDBStatus(
				IRodinDBStatusConstants.INVALID_ATTRIBUTE_VALUE, id));
	}

	private RodinDBException newInvalidKindException() {
		return new RodinDBException(new RodinDBStatus(
				IRodinDBStatusConstants.INVALID_ATTRIBUTE_KIND, id));
	}

	public java.lang.String toString(boolean value) throws RodinDBException {
		throw newInvalidKindException();
	}

	public java.lang.String toString(int value) throws RodinDBException {
		throw newInvalidKindException();
	}

	public java.lang.String toString(IRodinElement value)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	public java.lang.String toString(long value) throws RodinDBException {
		throw newInvalidKindException();
	}

	public java.lang.String toString(java.lang.String value)
			throws RodinDBException {
		throw newInvalidKindException();
	}

}
