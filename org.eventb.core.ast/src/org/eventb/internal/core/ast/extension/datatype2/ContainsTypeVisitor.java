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

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Type visitor that could be used to check if a type is contained in another.
 * 
 * @author Vincent Monfort
 */
public class ContainsTypeVisitor implements ITypeVisitor {

	private final Type isContained;
	private boolean containsType;

	public ContainsTypeVisitor(Type isContained) {
		this.isContained = isContained;
	}

	public boolean containsType(Type possibleContainer) {
		containsType = false;
		possibleContainer.accept(this);
		return containsType;
	}

	private void baseVisit(Type type) {
		if (type.equals(isContained)) {
			containsType = true;
		}
	}

	@Override
	public void visit(BooleanType type) {
		baseVisit(type);
	}

	@Override
	public void visit(GivenType type) {
		baseVisit(type);
	}

	@Override
	public void visit(IntegerType type) {
		baseVisit(type);
	}

	@Override
	public void visit(ParametricType type) {
		for (Type toVisit : type.getGivenTypes()) {
			containsType |= containsType(toVisit);
		}
	}

	@Override
	public void visit(PowerSetType type) {
		containsType = containsType(type.getBaseType());
	}

	@Override
	public void visit(ProductType type) {
		containsType = containsType(type.getLeft())
				|| containsType(type.getRight());
	}

}
