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

public final class Attribute<T_TYPE extends IAttributeType, T_VALUE> {
	final T_TYPE type;
	final T_VALUE value;

	public Attribute(T_TYPE type, T_VALUE value) {
		this.type = type;
		this.value = value;
	}

	public T_TYPE getType() {
		return type;
	}

	public T_VALUE getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute<?,?> att = (Attribute<?,?>) obj;
			return getType().equals(att.getType())
					&& getValue().equals(getValue());
		} else {
			return false;
		}
	}

}
