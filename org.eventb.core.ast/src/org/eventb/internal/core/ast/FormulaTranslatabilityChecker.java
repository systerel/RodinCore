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

import java.util.List;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Checks that a formula can be translated to some target factory.
 * 
 * Free identifiers are first checked using the cache implemented in the
 * formula. Then, we run an inspector on the formula to check types and
 * extensions. Types need only be verified on leaf nodes that can introduce new
 * types.
 * 
 * @author Vincent Monfort
 */
public class FormulaTranslatabilityChecker extends DefaultInspector<String> {

	/**
	 * Tells whether the given formula can be translated to the given factory.
	 * The third parameter is a direct access to the cache of free identifiers
	 * of the formula to avoid making a copy of this array.
	 * 
	 * @param formula
	 *            some formula
	 * @param factory
	 *            the target formula factory
	 * @param idents
	 *            a direct access to the cache of free identifiers of the given
	 *            formula
	 * @return <code>true</code> if the given formula can be translated to the
	 *         given factory
	 */
	public static boolean isTranslatable(Formula<?> formula,
			FormulaFactory factory, FreeIdentifier[] idents) {
		for (final FreeIdentifier ident : idents) {
			if (!factory.isValidIdentifierName(ident.getName())) {
				return false;
			}
		}
		final FormulaTranslatabilityChecker checker;
		checker = new FormulaTranslatabilityChecker(factory);
		final List<String> result = formula.inspect(checker);
		return result.isEmpty();

	}

	private final FormulaFactory factory;

	private FormulaTranslatabilityChecker(FormulaFactory factory) {
		this.factory = factory;
	}

	private void checkTypeTranslatable(Expression expr, IAccumulator<String> acc) {
		checkTypeTranslatable(expr.getType(), acc);
	}

	private void checkTypeTranslatable(Type type, IAccumulator<String> acc) {
		if (type != null && !type.isTranslatable(factory)) {
			acc.add("Incompatible type: " + type);
		}
	}

	private void checkExtensionTranslatable(IExtendedFormula extForm,
			IAccumulator<String> acc) {
		final IFormulaExtension extension = extForm.getExtension();
		if (!factory.hasExtension(extension)) {
			acc.add("Incompatible extension: " + extension);
		}
	}

	@Override
	public void inspect(AtomicExpression expr, IAccumulator<String> accumulator) {
		checkTypeTranslatable(expr, accumulator);
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<String> accumulator) {
		checkTypeTranslatable(decl.getType(), accumulator);
	}

	@Override
	public void inspect(BoundIdentifier ident, IAccumulator<String> accumulator) {
		checkTypeTranslatable(ident, accumulator);
	}

	@Override
	public void inspect(ExtendedExpression expr,
			IAccumulator<String> accumulator) {
		checkExtensionTranslatable(expr, accumulator);
		checkTypeTranslatable(expr, accumulator);
	}

	@Override
	public void inspect(ExtendedPredicate pred, IAccumulator<String> accumulator) {
		checkExtensionTranslatable(pred, accumulator);
	}

	@Override
	public void inspect(FreeIdentifier ident, IAccumulator<String> accumulator) {
		checkTypeTranslatable(ident, accumulator);
	}

	@Override
	public void inspect(SetExtension expr, IAccumulator<String> accumulator) {
		checkTypeTranslatable(expr, accumulator);
	}

}