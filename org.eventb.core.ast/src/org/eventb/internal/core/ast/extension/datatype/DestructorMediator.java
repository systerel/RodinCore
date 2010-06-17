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

import java.util.Map;

import org.eventb.core.ast.extension.datatype.IDestructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 *
 */
public class DestructorMediator extends DatatypeMediator implements IDestructorMediator {


	public DestructorMediator(String typeName,
			Map<String, ITypeParameter> typeParams) {
		super(typeName, typeParams);
	}

	public void addDestructor(String name, String id, ITypeParameter returnType) {
		// TODO Auto-generated method stub
		
	}

}
