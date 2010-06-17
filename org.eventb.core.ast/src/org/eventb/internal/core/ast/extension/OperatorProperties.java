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

import org.eventb.core.ast.extension.IOperatorProperties;

/**
 * @author Nicolas Beauger
 *
 */
public class OperatorProperties implements IOperatorProperties {

	private final Notation notation;
	private final FormulaType formulaType;
	private final Arity arity;
	private final FormulaType argumentType;

	private OperatorProperties(Notation notation, FormulaType formulaType,
			Arity arity, FormulaType argumentType) {
		this.notation = notation;
		this.formulaType = formulaType;
		this.arity = arity;
		this.argumentType = argumentType;
	}
	
	public Notation getNotation() {
		return notation;
	}
	
	public FormulaType getFormulaType() {
		return formulaType;
	}

	public Arity getArity() {
		return arity;
	}
	
	public FormulaType getArgumentType() {
		return argumentType;
	}

	public static IOperatorProperties makeOperProps(Notation notation,
			FormulaType formulaType, Arity arity, FormulaType argumentType) {
		return new OperatorProperties(notation, formulaType, arity,
				argumentType);
	}
}
