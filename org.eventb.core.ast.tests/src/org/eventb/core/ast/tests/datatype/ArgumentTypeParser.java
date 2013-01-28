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
package org.eventb.core.ast.tests.datatype;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.junit.Assert.assertFalse;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * Utility class for parsing the type of a constructor argument from a String.
 * 
 * FIXME I have the feeling that this should be in the AST.
 * 
 * @author Laurent Voisin
 */
public class ArgumentTypeParser implements ITypeVisitor {

	/**
	 * Returns the argument type parsed from the given string.
	 * 
	 * @param image
	 *            a string representation of an argument type
	 * @param mediator
	 *            the mediator used for building a datatype constructor
	 * @return the argument type parsed from the given string
	 */
	public static IArgumentType parseArgumentType(String image,
			IConstructorMediator mediator) {
		return new ArgumentTypeParser(mediator).parse(image);
	}

	private final IConstructorMediator mediator;
	private final FormulaFactory ff;

	// Result of the last transformation by the visitor
	private IArgumentType argType;

	public ArgumentTypeParser(IConstructorMediator mediator) {
		this.mediator = mediator;
		this.ff = mediator.getFactory();
	}

	/*
	 * We first parse the type as any type, and then construct the argument type
	 * by replacing given types by references to parameter types of the same
	 * name, if any.
	 */
	private IArgumentType parse(String image) {
		final IParseResult result = ff.parseType(image, V2);
		assertFalse(result.hasProblem());
		final Type type = result.getParsedType();
		type.accept(this);
		return argType;
	}

	@Override
	public void visit(BooleanType type) {
		argType = mediator.newArgumentType(type);
	}

	@Override
	public void visit(GivenType type) {
		final ITypeParameter param = mediator.getTypeParameter(type.getName());
		if (param != null) {
			argType = mediator.newArgumentType(param);
		} else {
			argType = mediator.newArgumentType(type);
		}
	}

	@Override
	public void visit(IntegerType type) {
		argType = mediator.newArgumentType(type);
	}

	@Override
	public void visit(ParametricType type) {
		final IExpressionExtension typeCons = type.getExprExtension();
		final Type[] typeParams = type.getTypeParameters();
		final int length = typeParams.length;
		final IArgumentType[] argTypeParams = new IArgumentType[length];
		for (int i = 0; i < length; i++) {
			typeParams[i].accept(this);
			argTypeParams[i] = argType;
		}
		argType = mediator.makeParametricType(typeCons, asList(argTypeParams));
	}

	@Override
	public void visit(PowerSetType type) {
		final Type base = type.getBaseType();
		final IArgumentType argBase = mediator.newArgumentType(base);
		argType = mediator.makePowerSetType(argBase);
	}

	@Override
	public void visit(ProductType type) {
		final Type left = type.getLeft();
		final Type right = type.getRight();
		final IArgumentType argLeft = mediator.newArgumentType(left);
		final IArgumentType argRight = mediator.newArgumentType(right);
		argType = mediator.makeProductType(argLeft, argRight);
	}

}
