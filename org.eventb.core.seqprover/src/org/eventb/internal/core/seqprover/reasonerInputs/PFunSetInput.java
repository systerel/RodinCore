/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.reasonerInputs;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;

/**
 * Input for reasoners that use an expression denoting a set of partial
 * functions.
 * 
 * @author Laurent Voisin
 */
public class PFunSetInput extends SingleExprInput {
	
	private Expression left;
	private Expression right;

	public PFunSetInput(String exprString, ITypeEnvironment typeEnv) {
		super(exprString, typeEnv);
		checkSetOfPartiaFunctions();
	}

	public PFunSetInput(Expression expression) {
		super(expression);
		checkSetOfPartiaFunctions();
	}

	public PFunSetInput(IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		checkSetOfPartiaFunctions();
	}
	
	private void checkSetOfPartiaFunctions() {
		if (hasError()) {
			return;
		}
		
		final Expression expr = getExpression();
		if (!Lib.isSetOfPartialFunction(expr)) {
			setError("Expected a set of all partial functions S ⇸ T");
			return;
		}
	 	left = ((BinaryExpression) expr).getLeft();
		right = ((BinaryExpression) expr).getRight();
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public Expression getRight(){
		return right;
	}
	
	@Override
	public IReasonerInput translate(FormulaFactory factory) {
		final SingleExprInput trSuper = (SingleExprInput) super.translate(factory);
		return new PFunSetInput(trSuper.getExpression());
	}

	// type environment does not change because left and right are subformulas
	// of the expression
	// so no need to override getTypeEnvironment()
}
