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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.extension.datatype.ITypeParameter;

public class TypeParam implements ITypeParameter {

	private final List<ITypeParameter> typeVars;
	private final String name;

	public TypeParam(String name) {
		this(name, Collections.<ITypeParameter> emptyList());
	}

	public TypeParam(String name, List<ITypeParameter> typeVars) {
		this.name = name;
		this.typeVars = new ArrayList<ITypeParameter>(typeVars);
	}

	public String getName() {
		return name;
	}
	
	public List<ITypeParameter> getChildParameters() {
		return Collections.unmodifiableList(typeVars);
	}

}