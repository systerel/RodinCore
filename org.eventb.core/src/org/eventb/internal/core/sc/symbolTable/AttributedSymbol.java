/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.sc.symbolTable.IAttributedSymbol;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class AttributedSymbol implements IAttributedSymbol {
	
	public AttributedSymbol() {
		types = new ArrayList<IAttributeType>(3);
		values = new ArrayList<Object>(3);
	}

	private final List<IAttributeType> types;
	private final List<Object> values;

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeTypes()
	 */
	public IAttributeType[] getAttributeTypes() {
		return types.toArray(new IAttributeType[types.size()]);
	}
	
	private int find(IAttributeType type) {
		int k = types.indexOf(type);
		if (k == -1) {
			throw new IllegalArgumentException("No attribute of type: " + type);
		} else {
			return k;
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeValue(org.rodinp.core.IAttributeType.Boolean)
	 */
	public boolean getAttributeValue(IAttributeType.Boolean type) {
		return (Boolean) values.get(find(type));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeValue(org.rodinp.core.IAttributeType.Handle)
	 */
	public IRodinElement getAttributeValue(IAttributeType.Handle type) {
		return (IRodinElement) values.get(find(type));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeValue(org.rodinp.core.IAttributeType.Integer)
	 */
	public int getAttributeValue(IAttributeType.Integer type) {
		return (java.lang.Integer) values.get(find(type));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeValue(org.rodinp.core.IAttributeType.Long)
	 */
	public long getAttributeValue(IAttributeType.Long type) {
		return (java.lang.Long) values.get(find(type));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#getAttributeValue(org.rodinp.core.IAttributeType.String)
	 */
	public String getAttributeValue(IAttributeType.String type) {
		return (String) values.get(find(type));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#hasAttribute(org.rodinp.core.IAttributeType)
	 */
	public boolean hasAttribute(IAttributeType type) {
		return types.contains(type);
	}
	
	protected void put(IAttributeType type, Object value) {
		int k = types.indexOf(type);
		if (k == -1) {
			types.add(type);
			values.add(value);
		} else {
			values.set(k, value);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#setAttributeValue(org.rodinp.core.IAttributeType.Boolean, boolean)
	 */
	public void setAttributeValue(IAttributeType.Boolean type, boolean newValue) {
		put(type, newValue);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#setAttributeValue(org.rodinp.core.IAttributeType.Handle, org.rodinp.core.IRodinElement)
	 */
	public void setAttributeValue(IAttributeType.Handle type, IRodinElement newValue) {
		put(type, newValue);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#setAttributeValue(org.rodinp.core.IAttributeType.Integer, int)
	 */
	public void setAttributeValue(IAttributeType.Integer type, int newValue) {
		put(type, newValue);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#setAttributeValue(org.rodinp.core.IAttributeType.Long, long)
	 */
	public void setAttributeValue(IAttributeType.Long type, long newValue) {
		put(type, newValue);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.IAttributedSymbol#setAttributeValue(org.rodinp.core.IAttributeType.String, java.lang.String)
	 */
	public void setAttributeValue(IAttributeType.String type,
			String newValue) {
		put(type, newValue);
	}

}
