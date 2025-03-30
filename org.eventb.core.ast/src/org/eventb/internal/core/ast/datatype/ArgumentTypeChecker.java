/*******************************************************************************
 * Copyright (c) 2013, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Checks that an argument type is well-formed, i.e. that recursive calls to the
 * datatype type do not occur directly or indirectly within a powerset
 * construct. Also tells whether there is any recursive occurrence.
 * 
 * @author Laurent Voisin
 */
public class ArgumentTypeChecker implements ITypeVisitor {

	// The type representing the datatype
	private final GivenType dtType;

	// Given types denoting type parameters
	private final Set<GivenType> typeParams;

	// Was the datatype found?
	private boolean found;

	// Number of enclosing power sets
	private int enclosingPowersets;

	public ArgumentTypeChecker(GivenType dtType, List<GivenType> params) {
		this.dtType = dtType;
		this.typeParams = new HashSet<>(params);
	}

	public void check(Type argType) {
		final FormulaFactory typeFactory = argType.getFactory();
		final FormulaFactory dtFactory = dtType.getFactory();
		if (typeFactory != dtFactory) {
			throw new IllegalArgumentException("The given argument type "
					+ argType + " has an incompatible factory: " + typeFactory
					+ " instead of the factory used to build the datatype: "
					+ dtFactory);
		}
		enclosingPowersets = 0;
		found = false;
		argType.accept(this);
	}

	public boolean isBasic() {
		return !found;
	}

	@Override
	public void visit(BooleanType type) {
		// do nothing
	}

	@Override
	public void visit(GivenType type) {
		if (dtType.equals(type)) {
			if (enclosingPowersets > 0) {
				throw new IllegalArgumentException(
						"The datatype type occurs within a powerset");
			}
			found = true;
		}
		else if (!typeParams.contains(type)) {
			throw new IllegalArgumentException(//
					"The given type '" + type.getName() + "' should be a type parameter");
		}
	}

	@Override
	public void visit(IntegerType type) {
		// do nothing
	}

	@Override
	public void visit(ParametricType type) {
		for (final Type child : type.getTypeParameters()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(PowerSetType type) {
		++enclosingPowersets;
		type.getBaseType().accept(this);
		--enclosingPowersets;
	}

	@Override
	public void visit(ProductType type) {
		type.getLeft().accept(this);
		type.getRight().accept(this);
	}

}
