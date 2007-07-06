package org.eventb.internal.pp.core.provers.seedsearch.solver;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;

public class Instantiable {
	// one instantiable per position/literal/clause combination

	private final LiteralSignature signature;
	// TODO replace by a clause-predicate 
	private final Clause clause;
	private final int predicatePosition;
	
	private final Set<Constant> doneInstantiations; 
	
	public Instantiable(LiteralSignature signature,
			Clause clause, int predicatePosition) {
		this.clause = clause;
		this.signature = signature;
		this.predicatePosition = predicatePosition;
		this.doneInstantiations = new HashSet<Constant>();
	}
	
	protected Instantiable(LiteralSignature signature) {
		this.signature = signature;
		predicatePosition = 0;
		clause = null;
		this.doneInstantiations = new HashSet<Constant>();
	}
	
	public void addDoneInstantiation(Constant constant) {
		doneInstantiations.add(constant);
	}
	
	public boolean hasInstantiation(Constant value) {
		return doneInstantiations.contains(value);
	}
	
	LiteralSignature getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return signature.toString();
	}
	
	public int getPosition() {
		return signature.getPosition();
	}
	
	public int getPredicatePosition() {
		return predicatePosition;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Instantiable) {
			Instantiable temp = (Instantiable) obj;
			return clause.equalsWithLevel(temp.clause) && predicatePosition == temp.predicatePosition
				&& signature.equals(temp.signature);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 37*(37*clause.hashCode()+signature.hashCode())+predicatePosition;
	}
	
}
