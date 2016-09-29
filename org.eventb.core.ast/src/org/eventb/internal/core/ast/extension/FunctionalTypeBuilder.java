/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;

/**
 * A class which allows to build the type of a function or constant that
 * replaces a mathematical extension.
 * 
 * @author Thomas Muller
 * @author Laurent Voisin
 */
public class FunctionalTypeBuilder {

	// The formula factory of the target language
	private final FormulaFactory factory;

	public FunctionalTypeBuilder(FormulaFactory factory) {
		this.factory = factory;
	}

	public Type makeFunctionalType(Type[] children, int numberOfPredicates,
			Type range) {
		final Type trgDomain = makeDomainType(children, numberOfPredicates);
		final Type trgRange = range.translate(factory);
		if (trgDomain == null) {
			// Atomic operator
			return trgRange;
		}
		return factory.makeRelationalType(trgDomain, trgRange);
	}

	private Type makeDomainType(Type[] children, int numberOfPredicates) {
		Type result = null;
		for (Type child : children) {
			final Type trgChild = child.translate(factory);
			result = join(result, trgChild);
		}
		final Type boolType = factory.makeBooleanType();
		for (int i = 0; i < numberOfPredicates; i++) {
			result = join(result, boolType);
		}
		return result;
	}

	private Type join(Type left, Type right) {
		if (left == null) {
			return right;
		}
		return factory.makeProductType(left, right);
	}

}