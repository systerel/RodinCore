/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype2;

import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.TypeRewriter;

/**
 * This class provides the way to substitute the types used to represent
 * datatype type and type parameters during the datatype construction with the
 * instantiated types corresponding.
 * 
 * @author Vincent Monfort
 */
final class TypeSubstitutionRewriter extends TypeRewriter {

	private final Map<GivenType, Type> substitutions;

	public TypeSubstitutionRewriter(FormulaFactory ff,
			Map<GivenType, Type> instantiated) {
		super(ff);
		substitutions = instantiated;
	}

	@Override
	public void visit(GivenType type) {
		// If the given type was one representing the datatype type or a type
		// parameter, substitute it. Otherwise just rewrite with the current
		// factory.
		if (substitutions.containsKey(type)) {
			result = substitutions.get(type);
		} else {
			super.visit(type);
		}
	}

}
