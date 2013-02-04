/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
public class MultipleExprInput implements IReasonerInput{
	
	private Expression[] expressions;
	private String error;
	
	public MultipleExprInput(String[] exprsString,BoundIdentDecl[] boundIdentDecls, ITypeEnvironment typeEnv){
		//	Parse and typecheck input
		expressions = new Expression[boundIdentDecls.length];
		for (int i = 0; i < boundIdentDecls.length; i++) {
			if ( i< exprsString.length &&
					exprsString[i] != null && 
					exprsString[i].trim().length() != 0)
			{
				final FormulaFactory ff = typeEnv.getFormulaFactory();
				final Expression expression = DLib.parseExpression(ff, 
						exprsString[i]);
				if (expression == null)
				{
					error = "Parse error for expression " + exprsString[i];
					expressions = null;
					return;
				}
				if (! Lib.isWellTypedInstantiation(expression,boundIdentDecls[i].getType(),typeEnv)) 
				{
					error = "Type check failed: " 
						+ exprsString[i]
						+ " expected type "
						+ boundIdentDecls[i].getType();
					expressions = null;
					return;
				}
				expressions[i] = expression;
			}
			else
				expressions[i] = null;
		}
		error = null;
	}
	
	public MultipleExprInput(Expression[] expressions){
		this.expressions = expressions;
		this.error = null;
	}
	
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the expressions.
	 */
	public final Expression[] getExpressions() {
		return expressions;
	}
	
	
	// returns instantiations corresponding to the bound ident decls, and null if mismatch
	public final Expression[] computeInstantiations(BoundIdentDecl[] boundIdentDecls){
		Expression[] instantiations = new Expression[boundIdentDecls.length];
		for (int i = 0; i < instantiations.length; i++) {
			if (i < expressions.length && expressions[i] != null)
			{
				if (! expressions[i].getType().
						equals(boundIdentDecls[i].getType()))
					return null;
				instantiations[i] = expressions[i];
			}
			else
			{
				instantiations[i]=null;
			}
		}
		return instantiations;
	}
	
	public void serialize(IReasonerInputWriter writer, String key)
			throws SerializeException {
		assert ! hasError();
		assert expressions != null;
		writer.putExpressions(key, expressions);
	}

	public MultipleExprInput(IReasonerInputReader reader, String key)
			throws SerializeException {

		expressions = reader.getExpressions(key);
		error = null;
	}

	public void applyHints(ReplayHints hints) {
		for (int i = 0; i < expressions.length; i++) {
			expressions[i] = hints.applyHints(expressions[i]);
		}
		
	}
}
