/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class TypeInstantiation {

	private final Map<ITypeParameter, Type> paramInst = new LinkedHashMap<ITypeParameter, Type>();

	public void put(ITypeParameter prm, Type type) {
		final Type old = paramInst.put(prm, type);
		if (old != null) {
			paramInst.put(prm, old);
			throw new IllegalArgumentException("overriding type for parameter "
					+ prm.getName());
		}
	}
	
	public Type get(ITypeParameter typeParam) {
		final Type type = paramInst.get(typeParam);
		if (type == null) {
			throw new IllegalArgumentException("unknown type parameter "
					+ typeParam.getName());
		}
		return type;
	}

	@Override
	public String toString() {
		return paramInst.toString();
	}

}
