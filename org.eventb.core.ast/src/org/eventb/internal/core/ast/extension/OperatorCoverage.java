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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * @author Nicolas Beauger
 * 
 */
public class OperatorCoverage {

	private final Notation notation;
	private final FormulaType formulaType;
	private final ArityCoverage exprArity;
	private final ArityCoverage predArity;
	private final ArityCoverage globalArity;
	private final boolean isAssociative;

	public OperatorCoverage(Notation notation, FormulaType formulaType,
			ArityCoverage exprArity, ArityCoverage predArity,
			ArityCoverage globalArity, boolean isAssociative) {
		this.notation = notation;
		this.formulaType = formulaType;
		this.exprArity = exprArity;
		this.predArity = predArity;
		this.globalArity = globalArity;
		this.isAssociative = isAssociative;
	}

	public boolean covers(IOperatorProperties properties) {
		final ITypeDistribution childTypes = properties.getChildTypes();
		return notation == properties.getNotation()
				&& formulaType == properties.getFormulaType()
				&& exprArity.contains(childTypes.getExprArity())
				&& predArity.contains(childTypes.getPredArity())
				&& isAssociative == properties.isAssociative();
	}

	public boolean conflictsWith(OperatorCoverage other) {
		return notation == other.notation
				&& formulaType == other.formulaType
				&& !exprArity.isDisjoint(other.exprArity)
				&& !predArity.isDisjoint(other.predArity)
				&& !globalArity.isDisjoint(other.globalArity)
				&& isAssociative == other.isAssociative;
	}

	public Notation getNotation() {
		return notation;
	}

	public FormulaType getFormulaType() {
		return formulaType;
	}

	public boolean isAssociative() {
		return isAssociative;
	}
	
	
}
