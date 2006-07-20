package org.eventb.core.prover.sequent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;

public class ProverSequent implements IProverSequent{
	
	private final ITypeEnvironment typeEnvironment;
	
	private final Set<Hypothesis> globalHypotheses;
	private final Set<Hypothesis> localHypotheses;
	
	private final Set<Hypothesis> hiddenHypotheses;
	private final Set<Hypothesis> selectedHypotheses;
	
	private final Predicate goal;

	
	public ITypeEnvironment typeEnvironment() {
		return this.typeEnvironment;
	}

	private Set<Hypothesis> hypothesesC;
	
	public Set<Hypothesis> hypotheses() {
		if (hypothesesC != null) return hypothesesC;
		hypothesesC = new HashSet<Hypothesis>(this.globalHypotheses);
		hypothesesC.addAll(this.localHypotheses);
		return hypothesesC;
	}
	
	private Set<Hypothesis> visibleHypothesesC;
	public Set<Hypothesis> visibleHypotheses() {
		if (visibleHypothesesC != null) return visibleHypothesesC;
		visibleHypothesesC = new HashSet<Hypothesis>(this.hypotheses());
		visibleHypothesesC.removeAll(this.hiddenHypotheses);
		return visibleHypothesesC;
	}
	

	
	public Predicate goal() {
		return this.goal;
	}
	
	public Set<Hypothesis> selectedHypotheses(){
		return selectedHypotheses;
	}
	
	public Set<Hypothesis> hiddenHypotheses(){
		return hiddenHypotheses;
	}
	
	public ProverSequent(ITypeEnvironment typeEnvironment,Set<Hypothesis> globalHypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.globalHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(globalHypotheses));
		this.localHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>());
		this.hiddenHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>());
		this.selectedHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>());
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
		
		// assert this.invariant();
	}
	
	private ProverSequent(ProverSequent pS){
		this.typeEnvironment = pS.typeEnvironment;
		this.globalHypotheses = pS.globalHypotheses;
		this.localHypotheses = pS.localHypotheses;
		this.hiddenHypotheses = pS.hiddenHypotheses;
		this.selectedHypotheses = pS.selectedHypotheses;
		this.goal = pS.goal;
	}
	
	private ProverSequent(ProverSequent pS, ITypeEnvironment typeEnvironment, Set<Hypothesis> globalHypotheses,
			Set<Hypothesis> localHypotheses, Set<Hypothesis> hiddenHypotheses, Set<Hypothesis> selectedHypotheses,
			Predicate goal){
		
		assert (pS != null) | (typeEnvironment != null & globalHypotheses != null & localHypotheses != null & 
				hiddenHypotheses != null & selectedHypotheses != null & goal != null);
		
		if (typeEnvironment == null) this.typeEnvironment = pS.typeEnvironment;
		else this.typeEnvironment = typeEnvironment.clone();
		
		if (globalHypotheses == null) this.globalHypotheses = pS.globalHypotheses;
		else this.globalHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(globalHypotheses));
		
		if (localHypotheses == null) this.localHypotheses = pS.localHypotheses;
		else this.localHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(localHypotheses));
		
		if (hiddenHypotheses == null) this.hiddenHypotheses = pS.hiddenHypotheses;
		else this.hiddenHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(hiddenHypotheses));
		
		if (selectedHypotheses == null) this.selectedHypotheses = pS.selectedHypotheses;
		else this.selectedHypotheses = Collections.unmodifiableSet(new HashSet<Hypothesis>(selectedHypotheses));
		
		if (goal == null) this.goal = pS.goal;
		else {
			assert goal.isTypeChecked();
			assert goal.isWellFormed();
			this.goal = goal;
		}
		
		assert this.hypotheses().containsAll(this.selectedHypotheses);
		assert this.hypotheses().containsAll(this.hiddenHypotheses);
		assert Collections.disjoint(this.selectedHypotheses,this.hiddenHypotheses);
		// assert this.invariant();
	}
	
	
	@Override
	public ProverSequent clone(){
		return new ProverSequent(this);
	}
	
	public ProverSequent InitialSequent(ITypeEnvironment typeEnvironment,Set<Hypothesis> globalHypotheses,Predicate goal){
		return new ProverSequent(typeEnvironment,globalHypotheses,goal);
	}
	
	public ProverSequent addHyps(Set<Hypothesis> hyps,ITypeEnvironment typeEnvironment){
		assert (hyps != null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		for (Hypothesis hyp : hyps) {
			if (! Lib.isWellTyped(hyp.getPredicate(),typeEnvironment)) return null;
		}
		Set<Hypothesis> newLocalHypotheses = new HashSet<Hypothesis>(this.localHypotheses);
		newLocalHypotheses.addAll(hyps);
		return new ProverSequent(this,typeEnvironment,null,newLocalHypotheses,null,null,null);
		
//		// Code for incremental type environments	
//		if (typeEnvironment == null)
//			return new SimpleProverSequent(this,null,null,newLocalHypotheses,null,null,null);	
//		ITypeEnvironment newITypeEnvironment = this.typeEnvironment.clone();
//		Set<FreeIdentifier> identifiers = typeEnvironment.variables();
//		for (FreeIdentifier i : identifiers){
//			if (newITypeEnvironment.contains(i)) assert newITypeEnvironment.getType(i).equals(typeEnvironment.getType(i));
//			else newITypeEnvironment.addIdent(i,typeEnvironment.getType(i));
//		}
//		return new SimpleProverSequent(this,newITypeEnvironment,null,newLocalHypotheses,null,null,null);
	}
	
	public ProverSequent addHyp(Hypothesis hyp,ITypeEnvironment typeEnvironment){
		assert (hyp != null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		if (! Lib.isWellTyped(hyp.getPredicate(),typeEnvironment)) return null;
		Set<Hypothesis> newLocalHypotheses = new HashSet<Hypothesis>(this.localHypotheses);
		newLocalHypotheses.add(hyp);
		return new ProverSequent(this,typeEnvironment,null,newLocalHypotheses,null,null,null);
		
//		// Code for incremental type environments
//		if (typeEnvironment == null)
//			return new SimpleProverSequent(this,null,null,newLocalHypotheses,null,null,null);
//		ITypeEnvironment newITypeEnvironment = this.typeEnvironment.clone();
//		Set<FreeIdentifier> identifiers = typeEnvironment.variables();
//		for (FreeIdentifier i : identifiers){
//			if (newITypeEnvironment.contains(i)) assert newITypeEnvironment.getType(i).equals(typeEnvironment.getType(i));
//			else newITypeEnvironment.addIdent(i,typeEnvironment.getType(i));
//		}
//		return new SimpleProverSequent(this,newITypeEnvironment,null,newLocalHypotheses,null,null,null);
	}
	
	public ProverSequent replaceGoal(Predicate goal,ITypeEnvironment typeEnvironment){
		assert (goal!=null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		if (! Lib.isWellTyped(goal,typeEnvironment)) return null;
		return new ProverSequent(this,typeEnvironment,null,null,null,null,goal);
		
//		// Code for incremental type environments
//		if (typeEnvironment == null)
//			return new SimpleProverSequent(this,null,null,null,null,null,goal);
//		ITypeEnvironment newITypeEnvironment = this.typeEnvironment.clone();
//		Set<FreeIdentifier> identifiers = typeEnvironment.variables();
//		for (FreeIdentifier i : identifiers){
//			if (newITypeEnvironment.contains(i)) assert newITypeEnvironment.getType(i).equals(typeEnvironment.getType(i));
//			else newITypeEnvironment.addIdent(i,typeEnvironment.getType(i));
//		}
//		return new SimpleProverSequent(this,newITypeEnvironment,null,null,null,null,goal);
	}
	
//	public IProverSequent buildOn(ITypeEnvironment addedTypeEnvironment, Set<Predicate> addedHyps, Predicate newGoal) {
//		assert (goal!=null);
//		return new SimpleProverSequent(this,)
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public ProverSequent hideHypotheses(Set<Hypothesis> toHide){
		// assert hypotheses().containsAll(toHide);
		// assert ! hiddenHypotheses.containsAll(toHide);
		Set<Hypothesis> newHiddenHypotheses = new HashSet<Hypothesis>(this.hiddenHypotheses);
		Set<Hypothesis> newSelectedHypotheses = new HashSet<Hypothesis>(this.selectedHypotheses);
		// newHiddenHypotheses.addAll(toHide);
		for (Hypothesis h:toHide){
			if (hypotheses().contains(h)){
				newHiddenHypotheses.add(h);
				newSelectedHypotheses.remove(h);
			}
		}
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
	}
	
	public ProverSequent showHypotheses(Set<Hypothesis> toShow){
		// assert hiddenHypotheses.containsAll(toShow);
		Set<Hypothesis> newHiddenHypotheses = new HashSet<Hypothesis>(this.hiddenHypotheses);
		newHiddenHypotheses.removeAll(toShow);
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,null,null);
	}
	
	public ProverSequent selectHypotheses(Collection<Hypothesis> toSelect){
		// assert hypotheses().containsAll(toSelect);
		Set<Hypothesis> newSelectedHypotheses = new HashSet<Hypothesis>(this.selectedHypotheses);
		Set<Hypothesis> newHiddenHypotheses = new HashSet<Hypothesis>(this.hiddenHypotheses);
		
		// newSelectedHypotheses.addAll(toSelect);
		for (Hypothesis h:toSelect){
			if (hypotheses().contains(h)){
				newSelectedHypotheses.add(h);
			}
		}
		newHiddenHypotheses.removeAll(toSelect);
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
	}
	
	public ProverSequent deselectHypotheses(Set<Hypothesis> toDeselect){
		// assert selectedHypotheses.containsAll(toDeselect);
		Set<Hypothesis> newSelectedHypotheses = new HashSet<Hypothesis>(this.selectedHypotheses);
		newSelectedHypotheses.removeAll(toDeselect);
		return new ProverSequent(this,null,null,null,null,newSelectedHypotheses,null);
	}
	
	@Override
	public String toString(){
		return (// this.getClass().toString() +
				typeEnvironment().toString() +
				hiddenHypotheses().toString() +
				visibleMinusSelectedHyps().toString() +
				selectedHypotheses().toString() + " |- " +
				goal().toString());
	}
	
	private Set<Hypothesis> visibleMinusSelectedHyps(){
		Set<Hypothesis> result = new HashSet<Hypothesis>(visibleHypotheses());
		result.removeAll(selectedHypotheses());
		return result;
	}
	
//	public boolean same(SimpleProverSequent S){
//		if (! this.goal().equals(S.goal())) return false;
//		if (! this.typeEnvironment().equals(S.typeEnvironment())) return false;
//		if (! this.hypotheses().equals(S.hypotheses())) return false;
//		return true;
//	}

}
