/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
public class SingleExprInput implements IReasonerInput, ITranslatableReasonerInput {
	
	private static final String SERIALIZATION_KEY = "expr";

	private Expression expression;
	private String error;
	
	public SingleExprInput(String exprString, ITypeEnvironment typeEnv){
		
		if (exprString.trim().length() == 0) {
			setError("Missing expression in Proof Control");
			return;
		}
		final FormulaFactory ff = typeEnv.getFormulaFactory();
		expression = DLib.parseExpression(ff, exprString);
		if (expression == null)
		{
			setError("Parse error for expression: " + exprString);
			return;
		}
		if (! Lib.typeCheckClosed(expression,typeEnv)){
			setError("Type check failed for expression: " + expression);
			return;
		}
		error = null;
	}
	
	public SingleExprInput(Expression expression){
		assert expression != null;
		this.expression = expression;
		this.error = null;
	}
	
	@Override
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	@Override
	public final String getError() {
		return error;
	}
	
	/**
	 * Allows subclasses to signal an error in this input. Must not be called if
	 * an error was already detected.
	 * 
	 * @param message
	 *            new error message
	 * @since 1.2
	 */
	protected void setError(String message) {
		if (message == null)
			throw new IllegalArgumentException("null error message");
		if (hasError()) 
			throw new IllegalStateException("An error was already detected.");
		expression = null;
		error = message;
	}

	/**
	 * @return Returns the Expression.
	 */
	public final Expression getExpression() {
		return expression;
	}

	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert expression != null;
		writer.putExpressions(SERIALIZATION_KEY, expression);
	}
	
	public SingleExprInput(IReasonerInputReader reader) throws SerializeException {
		final Expression[] preds = reader.getExpressions(SERIALIZATION_KEY);
		if (preds.length != 1) {
			throw new SerializeException(
					new IllegalStateException("Expected exactly one expression")
			);
		}
		expression = preds[0];
	}

	@Override
	public void applyHints(ReplayHints hints) {
		expression = hints.applyHints(expression);
		
	}
	
	/**
	 * @since 3.0
	 */
	@Override
	public IReasonerInput translate(FormulaFactory factory) {
		if (expression == null) {
			return this;
		}
		return new SingleExprInput(expression.translate(factory));
	}

	/**
	 * @since 3.0
	 */
	@Override
	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
		final ITypeEnvironmentBuilder typeEnv = factory
				.makeTypeEnvironment();
		if (expression != null) {
			typeEnv.addAll(expression.getFreeIdentifiers());
		}
		return typeEnv;
	}

}
