/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import static org.eventb.core.ast.extension.ExtensionFactory.makeAllExpr;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IFormulaExtension.ATOMIC_EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.ast.extension.datatype.TypeConstrMediator.TypeConstructor;
import org.eventb.internal.core.parser.BMath;

/**
 * @author Nicolas Beauger
 * 
 */
public class DatatypeExtensionComputer {

	private final IDatatypeExtension extension;
	private final FormulaFactory factory;

	public DatatypeExtensionComputer(IDatatypeExtension extension, FormulaFactory factory) {
		this.extension = extension;
		this.factory = factory;
	}

	public IDatatype compute() {
		final Map<String, IExpressionExtension> result = new HashMap<String, IExpressionExtension>();
		final TypeConstrMediator typeMed = new TypeConstrMediator(extension);
		extension.addTypeParameters(typeMed);
		final TypeConstructor typeConstructor = typeMed.getTypeConstructor();
		assert typeConstructor.isATypeConstructor();
		addExtension(result, typeConstructor);
		final List<ITypeParameter> typeParams = typeMed.getTypeParams();
		final Datatype datatype = new Datatype(typeConstructor, typeParams);
		typeConstructor.setOrigin(datatype);
		final ConstructorMediator consMed = new ConstructorMediator(datatype,
				factory);
		extension.addConstructors(consMed);

		return datatype;
	}

	private static void addExtension(Map<String, IExpressionExtension> map,
			IExpressionExtension extension) {
		map.put(extension.getId(), extension);
	}
	
	public static String computeGroup(int nbArgs) {
		final String groupId;
		if (nbArgs == 0) {
			groupId = BMath.ATOMIC_EXPR;
		} else {
			groupId = BMath.BOUND_UNARY;
		}
		return groupId;
	}
	
	public static IExtensionKind computeKind(int nbArgs) {
		final IExtensionKind kind;
		if (nbArgs == 0) {
			kind = ATOMIC_EXPRESSION;
		} else {
			kind = makePrefixKind(EXPRESSION,
					makeAllExpr(makeFixedArity(nbArgs)));
		}
		return kind;
	}

}
