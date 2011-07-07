/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import java.util.ArrayList;

import org.eventb.internal.ui.Pair;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class EventBAttributesManager {

	ArrayList<Pair<IAttributeType.Boolean, Boolean>> listBoolean;
	ArrayList<Pair<IAttributeType.Handle, IRodinElement>> listHandle;
	ArrayList<Pair<IAttributeType.Integer, Integer>> listInteger;
	ArrayList<Pair<IAttributeType.Long, Long>> listLong;
	ArrayList<Pair<IAttributeType.String, String>> listString;

	private ArrayList<IAttributeType> attributeTypes;

	public EventBAttributesManager(IInternalElement element)
			throws RodinDBException {
		initList();
		attributeTypes = new ArrayList<IAttributeType>();
		for (IAttributeType type : element.getAttributeTypes()) {
			attributeTypes.add(type);
			getAttributes(element, type);
		}
	}

	public EventBAttributesManager() {
		initList();
		attributeTypes = new ArrayList<IAttributeType>();
	}

	private void initList() {
		listBoolean = new ArrayList<Pair<IAttributeType.Boolean, Boolean>>();
		listHandle = new ArrayList<Pair<IAttributeType.Handle, IRodinElement>>();
		listInteger = new ArrayList<Pair<IAttributeType.Integer, Integer>>();
		listLong = new ArrayList<Pair<IAttributeType.Long, Long>>();
		listString = new ArrayList<Pair<IAttributeType.String, String>>();
	}

	private void getAttributes(final IInternalElement element,
			IAttributeType type) throws RodinDBException {
		if (type instanceof IAttributeType.String) {
			final IAttributeType.String attrType = (IAttributeType.String) type;
			final String value = element.getAttributeValue(attrType);
			listString.add(new Pair<IAttributeType.String, String>(attrType,
					value));
		} else if (type instanceof IAttributeType.Boolean) {
			final IAttributeType.Boolean attrType = (IAttributeType.Boolean) type;
			final Boolean value = new Boolean(element
					.getAttributeValue(attrType));
			listBoolean.add(new Pair<IAttributeType.Boolean, Boolean>(attrType,
					value));
		} else if (type instanceof IAttributeType.Handle) {
			final IAttributeType.Handle attrType = (IAttributeType.Handle) type;
			final IRodinElement value = element.getAttributeValue(attrType);
			listHandle.add(new Pair<IAttributeType.Handle, IRodinElement>(
					attrType, value));
		} else if (type instanceof IAttributeType.Integer) {
			final IAttributeType.Integer attrType = (IAttributeType.Integer) type;
			final Integer value = new Integer(element
					.getAttributeValue(attrType));
			listInteger.add(new Pair<IAttributeType.Integer, Integer>(attrType,
					value));
		} else if (type instanceof IAttributeType.Long) {
			final IAttributeType.Long attrType = (IAttributeType.Long) type;
			final Long value = new Long(element.getAttributeValue(attrType));
			listLong.add(new Pair<IAttributeType.Long, Long>(attrType, value));
		}
	}

	public ArrayList<Pair<IAttributeType.Boolean, Boolean>> getBooleanAttribute() {
		return listBoolean;
	}

	public ArrayList<Pair<IAttributeType.Handle, IRodinElement>> getHandleAttribute() {
		return listHandle;
	}

	public ArrayList<Pair<IAttributeType.Integer, Integer>> getIntegerAttribute() {
		return listInteger;
	}

	public ArrayList<Pair<IAttributeType.Long, Long>> getLongAttribute() {
		return listLong;
	}

	public ArrayList<Pair<IAttributeType.String, String>> getStringAttribute() {
		return listString;
	}

	public void addAttribute(IAttributeType.Boolean type, boolean value) {
		attributeTypes.add(type);
		listBoolean.add(new Pair<IAttributeType.Boolean, Boolean>(type,
				new Boolean(value)));
	}

	public void addAttribute(IAttributeType.Handle type, IInternalElement value) {
		if (value != null) {
			attributeTypes.add(type);
			listHandle.add(new Pair<IAttributeType.Handle, IRodinElement>(type,
					value));
		}
	}

	public void addAttribute(IAttributeType.Integer type, int value) {
		attributeTypes.add(type);
		listInteger.add(new Pair<IAttributeType.Integer, Integer>(type, value));
	}

	public void addAttribute(IAttributeType.Long type, long value) {
		attributeTypes.add(type);
		listLong
				.add(new Pair<IAttributeType.Long, Long>(type, new Long(value)));
	}

	public void addAttribute(IAttributeType.String type, String value) {
		if (value != null) {
			attributeTypes.add(type);
			listString
					.add(new Pair<IAttributeType.String, String>(type, value));
		}
	}

	public ArrayList<IAttributeType> getAttributeTypes() {
		return attributeTypes;
	}

}
