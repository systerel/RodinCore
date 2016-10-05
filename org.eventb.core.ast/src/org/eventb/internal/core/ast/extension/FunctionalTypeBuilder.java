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
import org.eventb.internal.core.ast.TypeRewriter;

/**
 * A class which allows to build the type of a function or constant that
 * replaces a mathematical extension.
 * 
 * @author Thomas Muller
 * @author Laurent Voisin
 */
public class FunctionalTypeBuilder {

	// How to rewrite the types coming from the source language.
	private final TypeRewriter rewriter;

	// The formula factory of the target language
	private final FormulaFactory factory;

	public FunctionalTypeBuilder(TypeRewriter rewriter) {
		this.rewriter = rewriter;
		this.factory = rewriter.getFactory();
	}

	public Type makeFunctionalType(Type[] children, int numberOfPredicates,
			Type range) {
		final Type trgDomain = makeDomainType(children, numberOfPredicates);
		final Type trgRange = rewriter.rewrite(range);
		if (trgDomain == null) {
			// Atomic operator
			return trgRange;
		}
		return factory.makeRelationalType(trgDomain, trgRange);
	}

	public Type makeRelationalType(Type[] children, Type range) {
		final Type trgDomain = makeRelDomainType(children);
		final Type trgRange = rewriter.rewrite(range);
		if (trgDomain == null) {
			// Atomic operator
			return trgRange;
		}
		final Type trgBase = trgRange.getBaseType();
		assert trgBase != null;
		return factory.makeRelationalType(trgDomain, trgBase);
	}

	private Type makeDomainType(Type[] children, int numberOfPredicates) {
		Type result = null;
		for (Type child : children) {
			final Type trgChild = rewriter.rewrite(child);
			result = join(result, trgChild);
		}
		final Type boolType = factory.makeBooleanType();
		for (int i = 0; i < numberOfPredicates; i++) {
			result = join(result, boolType);
		}
		return result;
	}

	private Type makeRelDomainType(Type[] children) {
		Type result = null;
		for (final Type child : children) {
			final Type base = child.getBaseType();
			assert base != null;
			final Type trgChild = rewriter.rewrite(base);
			result = join(result, trgChild);
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