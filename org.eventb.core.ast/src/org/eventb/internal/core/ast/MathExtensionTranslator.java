/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import static org.eventb.internal.core.ast.FreshNameSolver.solve;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Abstract class of mathematical extension translators.
 * 
 * @author "Thomas Muller"
 */
public abstract class MathExtensionTranslator {

	// FIXME should come through the constructor
	private static final FormulaFactory targetFactory = FormulaFactory.getDefault();

	protected final FormulaFactory factory;
	protected final Set<String> usedNames;
	protected final Set<String> addedUsedNames = new HashSet<String>();
	protected final Map<IExpressionExtension, Expression> consReplacements = new HashMap<IExpressionExtension, Expression>();
	protected final Map<String, Expression> destReplacements = new HashMap<String, Expression>();
	
	public MathExtensionTranslator(FormulaFactory factory, Set<String> usedNames) {
		this.factory = factory;
		this.usedNames = usedNames;
	}

	public Set<String> getAddedUsedNames() {
		return addedUsedNames;
	}
	
	// Gets a fresh name from the given type symbol, adds it to the used names,
	// and constructs a given type with that fresh name
	protected final GivenType solveGivenType(String typeSymbol) {
		final String solvedTypeName = solve(typeSymbol, usedNames, targetFactory);
		addedUsedNames.add(solvedTypeName);
		return targetFactory.makeGivenType(solvedTypeName);
	}
	
	// Gets a fresh name from the given symbol, adds it to the used names,
	// and constructs a free identifier with that fresh name and the given type
	protected final FreeIdentifier solveIdentifier(String symbol, Type type) {
		final String name = solve(symbol, usedNames, targetFactory);
		addedUsedNames.add(name);
		return targetFactory.makeFreeIdentifier(name, null, type);
	}

}
