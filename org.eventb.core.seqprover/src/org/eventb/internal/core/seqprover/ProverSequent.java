package org.eventb.internal.core.seqprover;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
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

	private static final Set<Predicate> NO_HYPS = Collections.unmodifiableSet(new HashSet<Predicate>());
	private static final FormulaFactory FORMULA_FACTORY = FormulaFactory.getDefault();

	
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
	
	public ProverSequent(ITypeEnvironment typeEnvironment,Collection<Predicate> globalHypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment == null ? FORMULA_FACTORY.makeTypeEnvironment() : typeEnvironment.clone();
		this.globalHypotheses = globalHypotheses == null ? NO_HYPS : Collections.unmodifiableSet(new HashSet<Predicate>(globalHypotheses));
		this.localHypotheses = NO_HYPS;
		this.hiddenHypotheses = NO_HYPS;
		this.selectedHypotheses = NO_HYPS;
		this.goal = goal;
		// assert this.invariant();
	}
	
	public ProverSequent(ITypeEnvironment typeEnvironment,Collection<Predicate> globalHypotheses, Collection<Predicate> selectedHypotheses,Predicate goal){
		this.typeEnvironment = typeEnvironment == null ? FORMULA_FACTORY.makeTypeEnvironment() : typeEnvironment.clone();
		this.globalHypotheses = globalHypotheses == null ? NO_HYPS : Collections.unmodifiableSet(new HashSet<Predicate>(globalHypotheses));
		this.localHypotheses = NO_HYPS;
		this.hiddenHypotheses = NO_HYPS;
		this.selectedHypotheses = selectedHypotheses == null ? NO_HYPS : Collections.unmodifiableSet(new HashSet<Predicate>(selectedHypotheses));
		// TODO : assert that hyps contains selected
		this.goal = goal;
		
		// assert this.invariant();
	}
	
	
	// does not clone
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
	
	public ProverSequent replaceGoal(Predicate goal,ITypeEnvironment typeEnvironment){
		assert (goal!=null);
		if (typeEnvironment == null) typeEnvironment = this.typeEnvironment;
		if (! typeCheckClosed(goal,typeEnvironment)) return null;
		return new ProverSequent(this,typeEnvironment,null,null,null,null,goal);
	}
	


	public IInternalProverSequent modify(FreeIdentifier[] freshFreeIdents, Collection<Predicate> addhyps, Predicate newGoal) {
		boolean modified = false;
		ITypeEnvironment newTypeEnv = typeEnvironment;
		Set<Predicate> newLocalHypotheses = null;
		Set<Predicate> newSelectedHypotheses = null;
		Set<Predicate> newHiddenHypotheses = null;
		if (freshFreeIdents != null && freshFreeIdents.length != 0)
		{
			newTypeEnv = typeEnvironment.clone();
			for (FreeIdentifier freshFreeIdent : freshFreeIdents) {
				if (newTypeEnv.contains(freshFreeIdent.getName())) return null;
				newTypeEnv.add(freshFreeIdent);
				modified = true;
			}
		}
		if (addhyps != null && addhyps.size() != 0) {
			newLocalHypotheses = new HashSet<Predicate>(localHypotheses);
			newSelectedHypotheses = new HashSet<Predicate>(selectedHypotheses);
			newHiddenHypotheses = new HashSet<Predicate>(hiddenHypotheses);
			for (Predicate hyp : addhyps) {
				if (! typeCheckClosed(hyp,newTypeEnv)) return null;
				if (! this.containsHypothesis(hyp)){
					newLocalHypotheses.add(hyp);
					modified = true;
				}
				modified |= newSelectedHypotheses.add(hyp);
				modified |= newHiddenHypotheses.remove(hyp);
			}
		}
		if (newGoal != null && ! newGoal.equals(goal)) {
			if (! typeCheckClosed(newGoal,newTypeEnv)) return null;
			modified = true;
		}
		
		if (modified) {
			return new ProverSequent(this, newTypeEnv, null,
					newLocalHypotheses, newHiddenHypotheses,
					newSelectedHypotheses, newGoal);
		}
		return this;
	}
		
	public ProverSequent selectHypotheses(Collection<Predicate> toSelect){
		if (toSelect == null) return this;
		boolean modified = false;
		
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		
		for (Predicate hyp:toSelect){
			if (hypotheses().contains(hyp)){
				modified |= newSelectedHypotheses.add(hyp);
				modified |= newHiddenHypotheses.remove(hyp);
			}
		}
		if (modified) return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
		return this;
	}
	
	public ProverSequent deselectHypotheses(Collection<Predicate> toDeselect){
		if (toDeselect == null) return null;
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
		boolean modified = newSelectedHypotheses.removeAll(toDeselect);
		if (modified) return new ProverSequent(this,null,null,null,null,newSelectedHypotheses,null);
		return this;
	}
	
	
	public ProverSequent hideHypotheses(Collection<Predicate> toHide){
		if (toHide == null) return this;
		boolean modified = false;
		
		Set<Predicate> newSelectedHypotheses = new HashSet<Predicate>(this.selectedHypotheses);
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		
		for (Predicate hyp:toHide){
			if (hypotheses().contains(hyp)){
				modified |= newHiddenHypotheses.add(hyp);
				modified |= newSelectedHypotheses.remove(hyp);
			}
		}
		if (modified) return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
		return this;
	}
	
	public ProverSequent showHypotheses(Collection<Predicate> toShow){
		if (toShow == null) return null;
		Set<Predicate> newHiddenHypotheses = new HashSet<Predicate>(this.hiddenHypotheses);
		boolean modified = newHiddenHypotheses.removeAll(toShow);
		if (modified) return new ProverSequent(this,null,null,null,newHiddenHypotheses,null,null);
		return this;
	}

	public IInternalProverSequent performfwdInf(Collection<Predicate> hyps, FreeIdentifier[] addedIdents, Collection<Predicate> infHyps) {
		boolean modified = false;
		
		ITypeEnvironment newTypeEnv = typeEnvironment;
		if (addedIdents != null)
		{
			newTypeEnv = typeEnvironment.clone();
			for (FreeIdentifier addedIdent : addedIdents) {
				if (newTypeEnv.contains(addedIdent.getName())) return this;
				newTypeEnv.add(addedIdent);
				modified = true;
			}
		}
		
		if (hyps != null && ! this.containsHypotheses(hyps)) return this;

		boolean selectInfHyps = true;
		boolean hideInfHyps = false;
		
		if (hyps != null) {
			selectInfHyps = ! Collections.disjoint(hyps, selectedHypotheses);
			hideInfHyps = selectInfHyps ? false : hiddenHypotheses.containsAll(hyps);
		}
		
		HashSet<Predicate> newLocalHypotheses = null;
		HashSet<Predicate> newSelectedHypotheses = null;
		HashSet<Predicate> newHiddenHypotheses = null;
		
		if (infHyps != null){
			newLocalHypotheses = new HashSet<Predicate>(localHypotheses);
			newSelectedHypotheses = new HashSet<Predicate>(selectedHypotheses);
			newHiddenHypotheses = new HashSet<Predicate>(hiddenHypotheses);
			for (Predicate infHyp : infHyps) {
				if (! typeCheckClosed(infHyp,newTypeEnv)) return this;
				if (! this.containsHypothesis(infHyp)){
					newLocalHypotheses.add(infHyp);
					if (selectInfHyps) newSelectedHypotheses.add(infHyp);
					if (hideInfHyps) newHiddenHypotheses.add(infHyp);
					modified = true;
				}
			}
		}		
		if (modified) return new ProverSequent(this,newTypeEnv,null,newLocalHypotheses,newHiddenHypotheses,newSelectedHypotheses,null);
		return this;
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

	public Iterable<Predicate> visibleHypIterable() {
		return visibleHypotheses();
	}
	
}
