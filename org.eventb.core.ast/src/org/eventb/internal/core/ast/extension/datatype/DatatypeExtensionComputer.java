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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

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

	public Map<String, IExpressionExtension> compute() {
		final Map<String, IExpressionExtension> result = new HashMap<String, IExpressionExtension>();
		final TypeConstrMediator typeMed = new TypeConstrMediator(extension);
		extension.addTypeParameters(typeMed);
		final IExpressionExtension typeConstructor = typeMed.getTypeConstructor();
		assert typeConstructor.isATypeConstructor();
		addExtension(result, typeConstructor);
		final List<ITypeParameter> typeParams = typeMed.getTypeParams();

		final ConstructorMediator consMed = new ConstructorMediator(typeConstructor,
				typeParams, factory);
		extension.addConstructors(consMed);
		addExtensions(result, consMed.getExtensions());

		return result;
	}

	private static void addExtensions(Map<String, IExpressionExtension> map,
			Set<IExpressionExtension> extensions) {
		for (IExpressionExtension extension : extensions) {
			addExtension(map, extension);
		}
	}

	private static void addExtension(Map<String, IExpressionExtension> map,
			IExpressionExtension extension) {
		map.put(extension.getId(), extension);
	}
	
}
