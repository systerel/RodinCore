package org.eventb.core.prover.reasoners;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.sequent.Hypothesis;

public class MultiplePredInput implements ReasonerInput{
	
	private Predicate[] predicates;
	private String error;
		
	public MultiplePredInput(Predicate[] predicates){
		this.predicates = predicates;
		if (this.predicates != null)
			this.error = null;
		else
			this.error = "Predicates uninitialised";
	}

	public MultiplePredInput(Set<Predicate> predicates){
		this(predicates.toArray(new Predicate[predicates.size()]));
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
	 * @return Returns the predicate.
	 */
	public final Predicate[] getPredicates() {
		return predicates;
	}

	public MultiplePredInput(SerializableReasonerInput serializableReasonerInput) {
		int length = Integer.parseInt(serializableReasonerInput.getString("length"));
		predicates = new Predicate[length];
		for (int i = 0; i < length; i++) {
			// null value taken care of in getExpression.
			predicates[i] = serializableReasonerInput.getPredicate(String.valueOf(i));
		}		
	}

	public SerializableReasonerInput genSerializable(){
		SerializableReasonerInput serializableReasonerInput 
		= new SerializableReasonerInput();
		assert predicates != null;
		serializableReasonerInput.putString("length",String.valueOf(predicates.length));
		for (int i = 0; i < predicates.length; i++) {
			// null value taken care of in putExpression.
			serializableReasonerInput.putPredicate(String.valueOf(i),predicates[i]);
		}
		return serializableReasonerInput;
	}
	
	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		assert ! hasError();
		assert predicates != null;
		reasonerInputSerializer.putString("length",String.valueOf(predicates.length));
		for (int i = 0; i < predicates.length; i++) {
			// null value taken care of in putExpression.
			reasonerInputSerializer.putPredicate(String.valueOf(i),predicates[i]);
		}
	}

	public MultiplePredInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		int length = Integer.parseInt(reasonerInputSerializer.getString("length"));
		Predicate[] predicates = new Predicate[length];
		for (int i = 0; i < length; i++) {
			// null value taken care of in getExpression.
			predicates[i] = reasonerInputSerializer.getPredicate(String.valueOf(i));
		}
		new MultiplePredInput(predicates);
	}

	public void applyHints(ReplayHints hints) {
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = hints.applyHints(predicates[i]);
		}
		
	}

}
