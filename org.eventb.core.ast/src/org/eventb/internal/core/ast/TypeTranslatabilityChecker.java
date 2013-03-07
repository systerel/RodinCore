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
package org.eventb.internal.core.ast;

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
 * Checks that a type can be translated to some target factory.
 * 
 * The check is performed by visiting the type and checking the following
 * conditions:
 * <ul>
 * <li>Names of given types are not reserved keywords of the target factory.</li>
 * <li>Parametric types use only extensions supported by the target factory.</li>
 * </ul>
 * 
 * @author Vincent Monfort
 */
public class TypeTranslatabilityChecker implements ITypeVisitor {

	/**
	 * Tells whether the given type can be translated to the given formula
	 * factory. The given type must not contained type variables.
	 * 
	 * @param type
	 *            some type that do not contain type variables
	 * @param target
	 *            some formula factory
	 * @return <code>true</code> iff the given type can be translated to the
	 *         given factory
	 */
	public static boolean isTranslatable(Type type, FormulaFactory target) {
		final TypeTranslatabilityChecker checker;
		checker = new TypeTranslatabilityChecker(target);
		type.accept(checker);
		return checker.isTranslatable();
	}

	private final FormulaFactory target;

	// Initially true, will become false if a problem is encountered
	private boolean isTranslatable = true;

	public TypeTranslatabilityChecker(FormulaFactory target) {
		this.target = target;
	}

	public boolean isTranslatable() {
		return isTranslatable;
	}

	@Override
	public void visit(BooleanType type) {
		// translation ok
	}

	@Override
	public void visit(GivenType type) {
		isTranslatable &= target.isValidIdentifierName(type.getName());
	}

	@Override
	public void visit(IntegerType type) {
		// translation ok
	}

	@Override
	public void visit(ParametricType type) {
		isTranslatable &= target.hasExtension(type.getExprExtension());
		for (Type child : type.getTypeParameters()) {
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