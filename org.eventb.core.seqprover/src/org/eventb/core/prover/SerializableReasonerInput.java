package org.eventb.core.prover;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class SerializableReasonerInput implements ReasonerInput {
	
	public HashMap<String,String> properties;
	public HashMap<String, Predicate> predicates;
	public HashMap<String, Expression> expressions;
	public Action hypAction;
	
	
	public SerializableReasonerInput(){
		properties = new HashMap<String,String>();
		predicates = new HashMap<String,Predicate>();
		expressions = new HashMap<String,Expression>();
		hypAction = null;
	}
	
	public void putString(String key,String value){
		if (value != null) properties.put(key,value);
	}
	
	public String getString(String key){
		return properties.get(key);
	}
	
	public void putPredicate(String key,Predicate value){
		if (value != null) predicates.put(key,value);
	}
	
	public Predicate getPredicate(String key){
		return predicates.get(key);
	}

	public void putExpression(String key,Expression value){
		if (value != null) expressions.put(key,value);
	}
	
	public Expression getExpression(String key){
		return expressions.get(key);
	}
	
	public SerializableReasonerInput genSerializable() {
		return this;
	}

	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		// TODO Auto-generated method stub
		
	}

	public void applyHints(ReplayHints hints) {
		// TODO Auto-generated method stub
		
	}

	public ReasonerInput[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
}
