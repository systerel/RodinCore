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

/**
 * @author Nicolas Beauger
 *
 */
public class OperatorProperties implements IOperatorProperties {

	private final Notation notation;
	private final FormulaType formulaType;
	private final ITypeDistribution childTypes;
	private final boolean isAssociative;

	private OperatorProperties(Notation notation, FormulaType formulaType,
			ITypeDistribution childTypes, boolean isAssociative) {
		this.notation = notation;
		this.formulaType = formulaType;
		this.childTypes = childTypes;
		this.isAssociative = isAssociative;
	}

	@Override
	public Notation getNotation() {
		return notation;
	}
	
	@Override
	public FormulaType getFormulaType() {
		return formulaType;
	}

	@Override
	public ITypeDistribution getChildTypes() {
		return childTypes;
	}
	
	@Override
	public boolean isAssociative() {
		return isAssociative;
	}

	public static IOperatorProperties makeOperProps(Notation notation,
			FormulaType formulaType, ITypeDistribution childTypes,
			boolean isAssociative) {
		return new OperatorProperties(notation, formulaType, childTypes,
				isAssociative);
	}
}
