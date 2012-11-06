/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - generic attribute manipulation
 *     Systerel - implementation of IAttributeType2
 *******************************************************************************/

package org.rodinp.internal.core;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.relations.api.IAttributeType2;
import org.rodinp.internal.core.relations.tomerge.InternalElementType2;
import org.rodinp.internal.core.util.Util;

/**
 * Implementation of attribute types.
 * 
 * @author Laurent Voisin
 */
public abstract class AttributeType<V> implements IAttributeType, IAttributeType2,
		IContributedItemType, Comparable<AttributeType<V>> {

	public static enum Kind {
		BOOLEAN("boolean") { //$NON-NLS-N$
			@Override
			protected AttributeType<?> makeType(java.lang.String typeId,
					java.lang.String typeName) {
				return new Boolean(typeId, typeName);
			}
		},
		HANDLE("handle") { //$NON-NLS-N$
			@Override
			protected AttributeType<?> makeType(java.lang.String typeId,
					java.lang.String typeName) {
				return new Handle(typeId, typeName);
			}
		},
		INTEGER("integer") { //$NON-NLS-N$
			@Override
			protected AttributeType<?> makeType(java.lang.String typeId,
					java.lang.String typeName) {
				return new Integer(typeId, typeName);
			}
		},
		LONG("long") { //$NON-NLS-N$
			@Override
			protected AttributeType<?> makeType(java.lang.String typeId,
					java.lang.String typeName) {
				return new Long(typeId, typeName);
			}
		},
		STRING("string") { //$NON-NLS-N$
			@Override
			protected AttributeType<?> makeType(java.lang.String typeId,
					java.lang.String typeName) {
				return new String(typeId, typeName);
			}
		},
		;

		private final java.lang.String name;

		private Kind(java.lang.String name) {
			this.name = name;
		}

		public static Kind fromName(java.lang.String name) {
			for (Kind kind : values()) {
				if (kind.name.equals(name)) {
					return kind;
				}
			}
			return null;
		}

		public java.lang.String getName() {
			return name;
		}

		protected abstract AttributeType<?> makeType(java.lang.String typeId,
				java.lang.String typeName);

	}

	public static class Boolean extends AttributeType<java.lang.Boolean> implements
			IAttributeType.Boolean {

		protected Boolean(java.lang.String id, java.lang.String name) {
			super(Kind.BOOLEAN, id, name);
		}

		@Override
		public AttributeValue.Boolean makeValue(boolean value) {
			return new AttributeValue.Boolean(this, value);
		}
		
		@Override
		public AttributeValue.Boolean makeValueFromRaw(
				java.lang.String rawValue) {
			return makeValue(parseValue(rawValue));
		}

		@Override
		@Deprecated
		public boolean getBoolValue(java.lang.String rawValue) {
			return parseValue(rawValue);
		}

		public boolean parseValue(java.lang.String rawValue) {
			return java.lang.Boolean.parseBoolean(rawValue);
		}

		@Override
		public java.lang.String toString(java.lang.Boolean value) {
			return java.lang.Boolean.toString(value);
		}

	}

	public static class Handle extends AttributeType<IRodinElement> implements
			IAttributeType.Handle {

		protected Handle(java.lang.String id, java.lang.String name) {
			super(Kind.HANDLE, id, name);
		}

		@Override
		public AttributeValue.Handle makeValue(IRodinElement value) {
			if (value == null) {
				throw new NullPointerException("null value");
			}
			return new AttributeValue.Handle(this, value);
		}
		
		@Override
		public AttributeValue.Handle makeValueFromRaw(
				java.lang.String rawValue) throws RodinDBException {
			return makeValue(parseValue(rawValue));
		}

		@Override
		@Deprecated
		public IRodinElement getHandleValue(java.lang.String rawValue)
				throws RodinDBException {
			return parseValue(rawValue);
		}

		public IRodinElement parseValue(java.lang.String rawValue)
				throws RodinDBException {
			final IRodinElement result = RodinCore.valueOf(rawValue);
			if (result == null) {
				Util.log(null, "Can't parse handle value for attribute " + id);
				throw newInvalidValueException();
			}
			return result;
		}

		@Override
		public java.lang.String toString(IRodinElement value) {
			return value.getHandleIdentifier();
		}

	}

	public static class Integer extends AttributeType<java.lang.Integer> implements
			IAttributeType.Integer {

		protected Integer(java.lang.String id, java.lang.String name) {
			super(Kind.INTEGER, id, name);
		}

		@Override
		public AttributeValue.Integer makeValue(int value) {
			return new AttributeValue.Integer(this, value);
		}

		@Override
		public AttributeValue.Integer makeValueFromRaw(
				java.lang.String rawValue) throws RodinDBException {
			return makeValue(parseValue(rawValue));
		}

		@Override
		@Deprecated
		public int getIntValue(java.lang.String rawValue)
				throws RodinDBException {
			return parseValue(rawValue);
		}

		public int parseValue(java.lang.String rawValue)
				throws RodinDBException {
			try {
				return java.lang.Integer.parseInt(rawValue);
			} catch (NumberFormatException e) {
				Util.log(e, "Can't parse integer value for attribute " + id);
				throw newInvalidValueException();
			}
		}

		@Override
		public java.lang.String toString(java.lang.Integer value) {
			return java.lang.Integer.toString(value);
		}

	}

	public static class Long extends AttributeType<java.lang.Long> implements
			IAttributeType.Long {

		protected Long(java.lang.String id, java.lang.String name) {
			super(Kind.LONG, id, name);
		}

		@Override
		public AttributeValue.Long makeValue(long value) {
			return new AttributeValue.Long(this, value);
		}

		@Override
		public AttributeValue.Long makeValueFromRaw(java.lang.String rawValue)
		throws RodinDBException {
			return makeValue(parseValue(rawValue));
		}
		
		@Override
		@Deprecated
		public long getLongValue(java.lang.String rawValue)
				throws RodinDBException {
			return parseValue(rawValue);
		}

		public long parseValue(java.lang.String rawValue)
				throws RodinDBException {
			try {
				return java.lang.Long.parseLong(rawValue);
			} catch (NumberFormatException e) {
				Util.log(e, "Can't parse long value for attribute " + id);
				throw newInvalidValueException();
			}
		}

		@Override
		public java.lang.String toString(java.lang.Long value) {
			return java.lang.Long.toString(value);
		}

	}

	public static class String extends AttributeType<java.lang.String> implements
			IAttributeType.String {

		protected String(java.lang.String id, java.lang.String name) {
			super(Kind.STRING, id, name);
		}

		@Override
		public AttributeValue.String makeValue(java.lang.String value) {
			if (value == null) {
				throw new NullPointerException("null value");
			}
			return new AttributeValue.String(this, value);
		}

		@Override
		public AttributeValue.String makeValueFromRaw(
				java.lang.String rawValue) {
			return makeValue(parseValue(rawValue));
		}
		
		@Override
		@Deprecated
		public java.lang.String getStringValue(java.lang.String rawValue) {
			return parseValue(rawValue);
		}

		public java.lang.String parseValue(java.lang.String rawValue) {
			return rawValue;
		}

		@Override
		public java.lang.String toString(java.lang.String value) {
			return value;
		}

	}

	public static AttributeType<?> valueOf(IConfigurationElement ice) {
		final java.lang.String localId = ice.getAttribute("id");
		if (localId == null) {
			logError(ice, "Missing id");
			return null;
		}
		if (containsDot(localId)) {
			logError(ice, "Invalid id (contains a dot): " + localId);
			return null;
		}
		if (containsWhiteSpace(localId)) {
			logError(ice, "Invalid id (contains white space): " + localId);
			return null;
		}
		final java.lang.String nameSpace = ice.getNamespaceIdentifier();
		final java.lang.String id = nameSpace + "." + localId;
		final java.lang.String name = ice.getAttribute("name");
		if (name == null) {
			logError(ice, "Missing name");
			return null;
		}
		final java.lang.String kindName = ice.getAttribute("kind");
		if (kindName == null) {
			logError(ice, "Missing kind");
			return null;
		}
		final Kind kind = Kind.fromName(kindName);
		if (kind == null) {
			logError(ice, "Unknown kind: " + kindName);
			return null;
		}
		return kind.makeType(id, name);
	}

	private static boolean containsDot(final java.lang.String str) {
		return str.indexOf('.') >= 0;
	}

	private static boolean containsWhiteSpace(final java.lang.String str) {
		for (int i = 0; i < str.length(); i = str.offsetByCodePoints(i, 1)) {
			final int cp = str.codePointAt(i);
			if (Character.isWhitespace(cp)) {
				return true;
			}
		}
		return false;
	}

	private static void logError(IConfigurationElement ice,
			java.lang.String message) {
		Util.log(null, "Error in attribute type contributed by \""
				+ ice.getContributor().getName() + "\":\n" + message);
	}

	private final Kind kind;

	protected final java.lang.String id;

	private final java.lang.String name;
	
	private List<InternalElementType2<?>> elementTypes = null;

	protected AttributeType(Kind kind, java.lang.String id,
			java.lang.String name) {
		this.kind = kind;
		this.id = id;
		this.name = name;
	}

	@Deprecated
	public boolean getBoolValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	@Deprecated
	public IRodinElement getHandleValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	@Override
	public final java.lang.String getId() {
		return id;
	}

	@Deprecated
	public int getIntValue(java.lang.String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	public Kind getKind() {
		return kind;
	}

	@Deprecated
	public long getLongValue(java.lang.String rawValue) throws RodinDBException {
		throw newInvalidKindException();
	}

	@Override
	public final java.lang.String getName() {
		return name;
	}

	@Deprecated
	public java.lang.String getStringValue(java.lang.String rawValue)
			throws RodinDBException {
		throw newInvalidKindException();
	}

	@Override
	public InternalElementType2<?>[] getElementTypes() {
		return elementTypes.toArray(new InternalElementType2<?>[elementTypes
				.size()]);
	}

	@Override
	public boolean isAttributeOf(IInternalElementType<?> elementType) {
		return elementTypes.contains(elementType);
	}

	protected RodinDBException newInvalidValueException() {
		return new RodinDBException(new RodinDBStatus(
				IRodinDBStatusConstants.INVALID_ATTRIBUTE_VALUE, id));
	}

	@Deprecated
	private RodinDBException newInvalidKindException() {
		return new RodinDBException(new RodinDBStatus(
				IRodinDBStatusConstants.INVALID_ATTRIBUTE_KIND, id));
	}
	
	public abstract AttributeValue<?, ?> makeValueFromRaw(
			java.lang.String rawValue) throws RodinDBException;

	public void setRelation(List<InternalElementType2<?>> eTypes) {
		if (elementTypes != null) {
			throw new IllegalStateException(
					"Illegal attempt to set relations for attribute type "
							+ getName());
		}
		this.elementTypes = eTypes;
	}

	public abstract java.lang.String toString(V value);

	@Override
	public int compareTo(AttributeType<V> other) {
		return this.id.compareTo(other.id);
	}

}
