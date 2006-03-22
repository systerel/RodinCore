package org.eventb.core.prover.sequent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

public class SimpleSequent implements ISequent {

	private final ITypeEnvironment typeEnvironment;
	private final Set<Hypothesis> hypotheses;
	private final Predicate goal;
	
	public SimpleSequent(ITypeEnvironment typeEnvironment,Set<Hypothesis> hypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.hypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(hypotheses));
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
	}
	
	public SimpleSequent(ITypeEnvironment typeEnvironment,Hypothesis hypothesis,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.hypotheses = Collections.singleton(hypothesis);
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
	}
	
	public SimpleSequent(ITypeEnvironment typeEnvironment,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.hypotheses = Collections.emptySet();
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
	}
	
//	public SimpleSequent(Set<Predicate> hypotheses,Predicate goal){
//		this.hypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(hypotheses));
//		this.goal = goal;
//		ITypeEnvironment typeEnvironment = new ITypeEnvironment();
//		goal.
//		typeEnvironment.
//		
//	}
	
	public ITypeEnvironment typeEnvironment() {
		return this.typeEnvironment;
	}

	
	public Set<Hypothesis> hypotheses() {
		return this.hypotheses;
	}

	
	public Predicate goal() {
		return this.goal;
	}
	
	
	@Override
	public String toString(){
		return (this.getClass().toString() +
				typeEnvironment().toString() +
				hypotheses().toString() +
				goal().toString());
	}

}
