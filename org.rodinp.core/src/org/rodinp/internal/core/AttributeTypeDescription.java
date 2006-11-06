/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.util.Util;

/**
 * Implementation of the description of an attribute type.
 * 
 * @author Laurent Voisin
 */
public abstract class AttributeTypeDescription {

	public static final String KIND_BOOLEAN = "boolean";
	public static final String KIND_HANDLE = "handle";
	public static final String KIND_INTEGER = "integer";
	public static final String KIND_LONG = "long";
	public static final String KIND_STRING = "string";

	private static class BoolAttributeTypeDescr extends AttributeTypeDescription {

		protected BoolAttributeTypeDescr(String id, String name) {
			super(id, name);
		}

		@Override
		public boolean getBoolValue(String rawValue) {
			return Boolean.valueOf(rawValue);
		}

		@Override
		public String getKind() {
			return KIND_BOOLEAN;
		}

		@Override
		public String toString(boolean value) throws RodinDBException {
			return Boolean.toString(value);
		}
		
	}
	
	private static class HandleAttributeTypeDescr extends AttributeTypeDescription {

		protected HandleAttributeTypeDescr(String id, String name) {
			super(id, name);
		}

		@Override
		public IRodinElement getHandleValue(String rawValue) throws RodinDBException {
			IRodinElement result = RodinCore.valueOf(rawValue);
			if (result == null) {
				Util.log(null, "Can't parse handle value for attribute " + id);
				throw newInvalidValueException();
			}
			return result;
		}

		@Override
		public String getKind() {
			return KIND_HANDLE;
		}

		@Override
		public String toString(IRodinElement value) throws RodinDBException {
			return value.getHandleIdentifier();
		}

	}

	private static class IntAttributeTypeDescr extends AttributeTypeDescription {

		protected IntAttributeTypeDescr(String id, String name) {
			super(id, name);
		}

		@Override
		public int getIntValue(String rawValue) throws RodinDBException {
			try {
				return Integer.valueOf(rawValue);
			} catch (NumberFormatException e) {
				throw newInvalidValueException();
			}
		}

		@Override
		public String getKind() {
			return KIND_INTEGER;
		}

		@Override
		public String toString(int value) throws RodinDBException {
			return Integer.toString(value);
		}
		
	}

	private static class LongAttributeTypeDescr extends AttributeTypeDescription {

		protected LongAttributeTypeDescr(String id, String name) {
			super(id, name);
		}

		@Override
		public long getLongValue(String rawValue) throws RodinDBException {
			try {
				return Long.valueOf(rawValue);
			} catch (NumberFormatException e) {
				throw newInvalidValueException();
			}
		}

		@Override
		public String getKind() {
			return KIND_LONG;
		}

		@Override
		public String toString(long value) throws RodinDBException {
			return Long.toString(value);
		}
		
	}

	private static class StringAttributeTypeDescr extends AttributeTypeDescription {

		protected StringAttributeTypeDescr(String id, String name) {
			super(id, name);
		}

		@Override
		public String getStringValue(String rawValue) {
			return rawValue;
		}

		@Override
		public String getKind() {
			return KIND_STRING;
		}

		@Override
		public String toString(String value) throws RodinDBException {
			return value;
		}
		
	}

	public static AttributeTypeDescription valueOf(IConfigurationElement ice) {
		final String nameSpace = ice.getNamespaceIdentifier();
		final String id = nameSpace + "." + ice.getAttribute("id");
		final String name = ice.getAttribute("name");
		final String kind = ice.getAttribute("kind");
		if (KIND_BOOLEAN.equals(kind)) {
			return new BoolAttributeTypeDescr(id, name);
		}
		if (KIND_HANDLE.equals(kind)) {
			return new HandleAttributeTypeDescr(id, name);
		}
		if (KIND_INTEGER.equals(kind)) {
			return new IntAttributeTypeDescr(id, name);
		}
		if (KIND_LONG.equals(kind)) {
			return new LongAttributeTypeDescr(id, name);
		}
		if (KIND_STRING.equals(kind)) {
			return new StringAttributeTypeDescr(id, name);
		}
		Util.log(null,
				"Unknown attribute kind " + kind +
				" when parsing configuration element " + id);
		return null;
	}

	protected final String id;
	
	protected final String name;
	
	protected AttributeTypeDescription(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public boolean getBoolValue(String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	public IRodinElement getHandleValue(String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	/**
	 * @return Returns the id.
	 */
	public final String getId() {
		return id;
	}
	
	public int getIntValue(String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	/**
	 * @return the kind of this attribute type
	 */
	public abstract String getKind(); 
	
	public long getLongValue(String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	/**
	 * @return Returns the name.
	 */
	public final String getName() {
		return name;
	}
	
	public String getStringValue(String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	protected RodinDBException newInvalidValueException() {
		return new RodinDBException(
				new RodinDBStatus(
						IRodinDBStatusConstants.INVALID_ATTRIBUTE_VALUE,
						id
				)
		);
	}
	
	private RodinDBException newInvalidKindException() {
		return new RodinDBException(
				new RodinDBStatus(
						IRodinDBStatusConstants.INVALID_ATTRIBUTE_KIND,
						id
				)
		);
	}
	
	public String toString(boolean value) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	public String toString(int value) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	public String toString(IRodinElement value) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	public String toString(long value) throws RodinDBException {
		throw newInvalidKindException();
	}
	
	public String toString(String value) throws RodinDBException {
		throw newInvalidKindException();
	}

}
