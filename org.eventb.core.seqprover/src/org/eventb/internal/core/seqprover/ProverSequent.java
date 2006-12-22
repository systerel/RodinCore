package org.eventb.internal.core.seqprover;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

public class ProverSequent implements IInternalProverSequent{
	
	// TODO : optimise this class
	
	private final ITypeEnvironment typeEnvironment;
	
	private final Set<Predicate> globalHypotheses;
	private final Set<Predicate> localHypotheses;
	
	private final Set<Predicate> hiddenHypotheses;
	private final Set<Predicate> selectedHypotheses;
	
	private final Predicate goal;
	
	
	public ITypeEnvironment typeEnvironment() {
		return this.typeEnvironment;
	}
	
	private Set<Predicate> hypothesesC;
	
	public Set<Predicate> hypotheses() {
		if (hypothesesC != null) return hypothesesC;
		hypothesesC = new HashSet<Predicate>(this.globalHypotheses);
		hypothesesC.addAll(this.localHypotheses);
		return hypothesesC;
	}
	
	private Set<Predicate> visibleHypothesesC;
	public Set<Predicate> visibleHypotheses() {
		if (visibleHypothesesC != null) return visibleHypothesesC;
		visibleHypothesesC = new HashSet<Predicate>(this.hypotheses());
		visibleHypothesesC.removeAll(this.hiddenHypotheses);
		return visibleHypothesesC;
	}
	
	
	
	public Predicate goal() {
		return this.goal;
	}
	
	public Set<Predicate> selectedHypotheses(){
		return selectedHypotheses;
	}
	
	public Set<Predicate> hiddenHypotheses(){
		return hiddenHypotheses;
	}
	
	public ProverSequent(ITypeEnvironment typeEnvironment,Set<Predicate> globalHypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.globalHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(globalHypotheses));
		this.localHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>());
		this.hiddenHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>());
		this.selectedHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>());
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
		
		// assert this.invariant();
	}
	
	public ProverSequent(ITypeEnvironment typeEnvironment,Set<Predicate> globalHypotheses, Set<Predicate> selectedHypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment.clone();
		this.globalHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(globalHypotheses));
		this.localHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>());
		this.hiddenHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>());
		this.selectedHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(selectedHypotheses));
		assert goal.isTypeChecked();
		assert goal.isWellFormed();
		this.goal = goal;
		
		// assert this.invariant();
	}
	
	
	
//	/**
//	* Copy constructor
//	* 
//	* @param pS
//	*/
//	private ProverSequent(ProverSequent pS){
//	this.typeEnvironment = pS.typeEnvironment;
//	this.globalHypotheses = pS.globalHypotheses;
//	this.localHypotheses = pS.localHypotheses;
//	this.hiddenHypotheses = pS.hiddenHypotheses;
//	this.selectedHypotheses = pS.selectedHypotheses;
//	this.goal = pS.goal;
//	}
	
	private ProverSequent(ProverSequent pS, ITypeEnvironment typeEnvironment, Set<Predicate> globalHypotheses,
			Set<Predicate> localHypotheses, Set<Predicate> hiddenHypotheses, Set<Predicate> selectedHypotheses,
			Predicate goal){
		
		assert (pS != null) | (typeEnvironment != null & globalHypotheses != null & localHypotheses != null & 
				hiddenHypotheses != null & selectedHypotheses != null & goal != null);
		
		if (typeEnvironment == null) this.typeEnvironment = pS.typeEnvironment;
		else this.typeEnvironment = typeEnvironment.clone();
		
		if (globalHypotheses == null) this.globalHypotheses = pS.globalHypotheses;
		else this.globalHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(globalHypotheses));
		
		if (localHypotheses == null) this.localHypotheses = pS.localHypotheses;
		else this.localHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(localHypotheses));
		
		if (hiddenHypotheses == null) this.hiddenHypotheses = pS.hiddenHypotheses;
		else this.hiddenHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(hiddenHypotheses));
		
		if (selectedHypotheses == null) this.selectedHypotheses = pS.selectedHypotheses;
		else this.selectedHypotheses = Collections.unmodifiableSet(new HashSet<Predicate>(selectedHypotheses));
		
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
	
	
	public ProverSequent addHyps(Collection<Predicate> hyps,ITypeEnvironment typeEnvironment){
		assert (hyps != null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		for (Predicate hyp : hyps) {
			if (! typeCheckClosed(hyp,typeEnvironment)) return null;
		}
		Set<Predicate> newLocalHypotheses = new HashSet<Predicate>(this.localHypotheses);
		newLocalHypotheses.addAll(hyps);
		return new ProverSequent(this,typeEnvironment,null,newLocalHypotheses,null,null,null);
	}
	
	public ProverSequent addHyp(Predicate hyp,ITypeEnvironment typeEnvironment){
		assert (hyp != null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		if (! typeCheckClosed(hyp,typeEnvironment)) return null;
		Set<Predicate> newLocalHypotheses = new HashSet<Predicate>(this.localHypotheses);
		newLocalHypotheses.add(hyp);
		return new ProverSequent(this,typeEnvironment,null,newLocalHypotheses,null,null,null);
	}
	
	public ProverSequent replaceGoal(Predicate goal,ITypeEnvironment typeEnvironment){
		assert (goal!=null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		if (! typeCheckClosed(goal,typeEnvironment)) return null;
		return new ProverSequent(this,typeEnvironment,null,null,null,null,goal);
	}
	
	public ProverSequent hideHypotheses(Collection<Predicate> toHide){
		// assert hypotheses().containsAll(toHide);
		// assert ! hiddenHypotheses.containsAll(toHide);
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
		// newHiddenHypotheses.addAll(toHide);
		for (Predicate h:toHide){
			if (hypotheses().contains(h)){
				newHiddenHypotheses.add(h);
				newSelectedHypotheses.remove(h);
			}
		}
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
	}
	
	public ProverSequent showHypotheses(Collection<Predicate> toShow){
		// assert hiddenHypotheses.containsAll(toShow);
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		newHiddenHypotheses.removeAll(toShow);
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,null,null);
	}
	
	public ProverSequent selectHypotheses(Collection<Predicate> toSelect){
		// assert hypotheses().containsAll(toSelect);
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		
		// newSelectedHypotheses.addAll(toSelect);
		for (Predicate h:toSelect){
			if (hypotheses().contains(h)){
				newSelectedHypotheses.add(h);
			}
		}
		newHiddenHypotheses.removeAll(toSelect);
		return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
	}
	
	public ProverSequent deselectHypotheses(Collection<Predicate> toDeselect){
		// assert selectedHypotheses.containsAll(toDeselect);
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
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
	
	private Set<Predicate> visibleMinusSelectedHyps(){
		Set<Predicate> result = new HashSet<Predicate>(visibleHypotheses());
		result.removeAll(selectedHypotheses());
		return result;
	}
	
	private static boolean typeCheckClosed(Formula f, ITypeEnvironment t) {
		ITypeCheckResult tcr = f.typeCheck(t);
		// new free variables introduced
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}



	public boolean containsHypothesis(Predicate predicate) {
		return hypotheses().contains(predicate);
	}

	public boolean containsHypotheses(Collection<Predicate> preds) {
		return hypotheses().containsAll(preds);
	}

	public Iterable<Predicate> hypIterable() {
		return hypotheses();
	}



	public Iterable<Predicate> hiddenHypIterable() {
		return hiddenHypotheses();
	}


	public Iterable<Predicate> selectedHypIterable() {
		return selectedHypotheses();
	}



	public boolean isHidden(Predicate hyp) {
		return hiddenHypotheses.contains(hyp);
	}



	public boolean isSelected(Predicate hyp) {
		return selectedHypotheses.contains(hyp);
	}



	
	
}
