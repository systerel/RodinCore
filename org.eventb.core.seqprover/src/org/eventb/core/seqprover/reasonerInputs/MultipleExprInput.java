package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class MultipleExprInput implements IReasonerInput{
	
	private Expression[] expressions;
	private String error;
	
	public MultipleExprInput(String[] exprsString,BoundIdentDecl[] boundIdentDecls, ITypeEnvironment typeEnv){
		//	Parse and typecheck input
		expressions = new Expression[boundIdentDecls.length];
		Expression expression;
		for (int i = 0; i < boundIdentDecls.length; i++) {
			if ( i< exprsString.length &&
					exprsString[i] != null && 
					exprsString[i].trim().length() != 0)
			{
				expression = Lib.parseExpression(exprsString[i]);
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
	
	// It is unsure if it is desired that reasoners modify their own input.
	@Deprecated
	public final void modifyToInstantiate(BoundIdentDecl[] boundIdentDecls){
		Expression[] instantiations = new Expression[boundIdentDecls.length];
		for (int i = 0; i < instantiations.length; i++) {
			if (i < expressions.length && expressions[i] != null)
			{
				if (! expressions[i].getType().
						equals(boundIdentDecls[i].getType()))
				{
					error = "Type check failed: " 
						+ expressions[i]
						+ " expected type "
						+ boundIdentDecls[i].getType();
					expressions = null;
					return;
				}
				instantiations[i] = expressions[i];
			}
			else
			{
				instantiations[i]=null;
			}
		}
		expressions = instantiations;
		error = null;
	}

	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		assert ! hasError();
		assert expressions != null;
		reasonerInputSerializer.putString("length",String.valueOf(expressions.length));
		for (int i = 0; i < expressions.length; i++) {
			// null value taken care of in putExpression.
			reasonerInputSerializer.putExpression(String.valueOf(i),expressions[i]);
		}
	}

	public MultipleExprInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		int length = Integer.parseInt(reasonerInputSerializer.getString("length"));
		expressions = new Expression[length];
		for (int i = 0; i < length; i++) {
			// null value taken care of in getExpression.
			expressions[i] = reasonerInputSerializer.getExpression(String.valueOf(i));
		}
		error = null;
	}

	public void applyHints(ReplayHints hints) {
		for (int i = 0; i < expressions.length; i++) {
			expressions[i] = hints.applyHints(expressions[i]);
		}
		
	}
}
