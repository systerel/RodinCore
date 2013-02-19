/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import java.util.ArrayList;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Here we reuse the implementation of type environments, just adding the
 * necessary machinery to take into account an initial type environment.
 * 
 * @author Vincent Monfort
 */
public class InferredTypeEnvironment extends TypeEnvironmentBuilder implements
		IInferredTypeEnvironment {

	private final ISealedTypeEnvironment initialTypeEnvironment;

	public InferredTypeEnvironment(ITypeEnvironment initialTypeEnvironment) {
		super(initialTypeEnvironment.getFormulaFactory());
		this.initialTypeEnvironment = initialTypeEnvironment.makeSnapshot();
	}

	@Override
	public ISealedTypeEnvironment getInitialTypeEnvironment() {
		return this.initialTypeEnvironment;
	}

	/*
	 * Looks up the given name in both type environments. Returns null if not
	 * found.
	 */
	@Override
	protected Type internalGetType(String name) {
		final Type type = this.getType(name);
		if (type != null) {
			return type;
		}
		return this.initialTypeEnvironment.getType(name);
	}

	// solves the unknown types (names who have type variable as their
	// corresponding type).
	public void solveVariables(TypeUnifier unifier) {
		// As setName() may change the map, we need to iterate in two passes
		final ArrayList<String> names = new ArrayList<String>(getNames());
		for (final String name : names) {
			setName(name, unifier.solve(getType(name)));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		final InferredTypeEnvironment other = (InferredTypeEnvironment) obj;
		return this.initialTypeEnvironment.equals(other.initialTypeEnvironment)
				&& super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + 17 * initialTypeEnvironment.hashCode();
	}

	@Override
	public final boolean isTranslatable(FormulaFactory fac) {
		return false;
	}

	@Override
	public ITypeEnvironment translate(FormulaFactory fac) {
		throw new UnsupportedOperationException(
				"An IInferredTypeEnvironment cannot be translated.");
	}

}
