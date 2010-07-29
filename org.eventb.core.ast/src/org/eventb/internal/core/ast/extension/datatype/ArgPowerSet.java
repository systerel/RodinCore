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

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.datatype.IArgumentType;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgPowerSet implements IArgumentType {

	private final IArgumentType arg;

	public ArgPowerSet(IArgumentType arg) {
		this.arg = arg;
	}
	
	@Override
	public Type toType(ITypeMediator mediator, TypeInstantiation instantiation) {
		final Type type = arg.toType(mediator, instantiation);
		return mediator.makePowerSetType(type);
	}

	@Override
	public boolean verifyType(Type proposedType, TypeInstantiation instantiation) {
		final Type baseType = proposedType.getBaseType();
		if (baseType == null) {
			return false;
		}
		return arg.verifyType(baseType, instantiation);
	}

}
