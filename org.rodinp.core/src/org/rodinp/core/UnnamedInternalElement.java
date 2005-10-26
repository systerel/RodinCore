/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

public class UnnamedInternalElement extends InternalElement {

	/* Type of this unnamed internal element. */ 
	private String type;

	public UnnamedInternalElement(String type, RodinElement parent) {
		super(RodinCore.getRodinCore().intern(REM_TYPE_PREFIX + type),
				parent);
		this.type = type;
	}

	public String getElementType() {
		return type;
	}

	@Override
	protected void getHandleMemento(StringBuffer buff) {
		getParent().getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(getElementName());
	}

}
