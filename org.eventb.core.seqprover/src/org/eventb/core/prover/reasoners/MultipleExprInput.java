package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public class MultipleExprInput implements ReasonerInput{
	
	private Expression[] expressions;
	private String error;
	
//	public MultipleExprInput(String predString, ITypeEnvironment typeEnv){
//		
//		predicate = Lib.parsePredicate(predString);
//		if (predString == null)
//		{
//			error = "Parse error for predicate: "+ predString;
//			return;
//		}
//		if (! Lib.typeCheckClosed(predicate,typeEnv)){
//			error = "Type check failed for Predicate: "+predicate;
//			predicate = null;
//			return;
//		}		
//	}
	
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

	public MultipleExprInput(SerializableReasonerInput serializableReasonerInput) {
		int length = Integer.parseInt(serializableReasonerInput.getString("length"));
		expressions = new Expression[length];
		for (int i = 0; i < length; i++) {
			// null value taken care of in getExpression.
			expressions[i] = serializableReasonerInput.getExpression(String.valueOf(i));
		}		
	}

	public SerializableReasonerInput genSerializable(){
		SerializableReasonerInput serializableReasonerInput 
		= new SerializableReasonerInput();
		assert expressions != null;
		serializableReasonerInput.putString("length",String.valueOf(expressions.length));
		for (int i = 0; i < expressions.length; i++) {
			// null value taken care of in putExpression.
			serializableReasonerInput.putExpression(String.valueOf(i),expressions[i]);
		}
		return serializableReasonerInput;
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
