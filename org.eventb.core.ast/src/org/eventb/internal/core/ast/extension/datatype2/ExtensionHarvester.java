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

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Harvests extensions used in some types. This is used to collect the
 * mathematical extensions that are used in the definition of a future datatype.
 * 
 * @author Laurent Voisin
 */
public class ExtensionHarvester implements ITypeVisitor {

	// Extensions encountered so far
	private final Set<IFormulaExtension> extensions;

	public ExtensionHarvester() {
		this.extensions = new HashSet<IFormulaExtension>();
	}

	public void harvest(Type type) {
		type.accept(this);
	}

	public Set<IFormulaExtension> getResult() {
		return extensions;
	}

	@Override
	public void visit(BooleanType type) {
		// do nothing
	}

	@Override
	public void visit(GivenType type) {
		// do nothing
	}

	@Override
	public void visit(IntegerType type) {
		// do nothing
	}

	@Override
	public void visit(ParametricType type) {
		extensions.add(type.getExprExtension());
		for (final Type child : type.getTypeParameters()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(PowerSetType type) {
		type.getBaseType().accept(this);
	}

	@Override
	public void visit(ProductType type) {
		type.getLeft().accept(this);
		type.getRight().accept(this);
	}

}
