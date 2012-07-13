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
package org.eventb.internal.core.ast.extension.datatype;

import static org.eventb.core.ast.Formula.KPARTITION;

import java.util.Iterator;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.internal.core.ast.MathExtensionTranslator;

/**
 * This class stores the translation of enumeration datatypes for a given
 * instance of parametric type into Event-B constructs where no occurrence of
 * the parametric type exists. This class also allows one to retrieve the axioms
 * associated to this translation and generated to help proving.
 * 
 * ENUM(c1, c2, ..., ci) is translated to partition(τ, {μ1}, {μ2}, ... ,{μi})
 * where τ is the fresh new type corresponding to the parametric type
 * constructor, and μN is the fresh identifier corresponding to the datatype
 * constructor cN.
 * 
 * @author Thomas Muller
 */
public class EnumDatatypeTranslator extends MathExtensionTranslator{

	private final ParametricType parametricType;

	private Predicate partitionPredicate;

	public EnumDatatypeTranslator(FormulaFactory factory, Set<String> usedNames,
			ParametricType parametricType) {
		super(factory, usedNames);
		this.parametricType = parametricType;
		computeTranslation();
	}

	private void computeTranslation() {
		final IExpressionExtension typeCons = parametricType.getExprExtension();
		final Object origin = typeCons.getOrigin();
		assert (origin instanceof IDatatype);
		final IDatatype datatype = (IDatatype) origin;
		final Set<IExpressionExtension> cChildren = datatype.getConstructors();
		final Expression[] children = getPartitionChildren(typeCons, cChildren);
		partitionPredicate = factory.makeMultiplePredicate(KPARTITION,
				children, null);
	}

	public Predicate getTranslatedPredicate() {
		return partitionPredicate;
	}
	
	private Expression[] getPartitionChildren(
			final IExpressionExtension typeConstructor,
			final Set<IExpressionExtension> enums) {
		final Expression[] children = new Expression[1 + enums.size()];
		final String enumerationSymbol = typeConstructor.getSyntaxSymbol();
		final GivenType tau = solveGivenType(enumerationSymbol);
		final Expression tauExpression = tau.toExpression(factory);
		consReplacements.put(typeConstructor, tauExpression);
		children[0] = tauExpression;
		// we add datatype constructors as partition singleton elements
		int i = 1;
		final Iterator<IExpressionExtension> iter = enums.iterator();
		while (iter.hasNext()) {
			final IExpressionExtension enumElement = iter.next();
			children[i] = makeSingleton(enumElement, tau);
			i++;
		}
		return children;
	}

	// Returns free identified singleton made out of the given symbol that has
	// been solved and the given type.
	private SetExtension makeSingleton(IExpressionExtension constructor,
			GivenType type) {
		final String symbol = constructor.getSyntaxSymbol();
		final FreeIdentifier identifier = solveIdentifier(symbol, type);
		consReplacements.put(constructor, identifier);
		return factory.makeSetExtension(new Expression[] { identifier }, null);
	}

}
