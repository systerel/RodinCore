package org.eventb.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.prover.rules.AllI;
import org.eventb.core.prover.rules.ConjI;
import org.eventb.core.prover.rules.Hyp;
import org.eventb.core.prover.rules.ImpI;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.rules.Rule;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.tactics.Tactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.core.pm.HypothesisChangeEvent;
import org.eventb.internal.core.pm.HypothesisDelta;
import org.eventb.internal.core.pm.POChangeEvent;
import org.eventb.internal.core.pm.PODelta;
import org.eventb.internal.core.pm.ProofTreeChangeEvent;
import org.eventb.internal.core.pm.ProofTreeDelta;

public class UserSupport
{
	Collection<IHypothesisChangedListener> hypChangedListeners;
	Collection<IGoalChangedListener> goalChangedListeners;
	Collection<IPOChangedListener> poChangedListeners;
	Collection<IProofTreeChangedListener> proofTreeChangedListeners;
	
	private List<ProofState> proofStates;
	private int counter;
	
	private ProofState proofState;
	
	private static String [] names = {
			"1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2",
			"1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1 ⇒ x = 1))",
			"x=1 ∨x=2 |- x < 3 ",
			"1=1 |-  ∃x·x=1"
		};

	public UserSupport() {
		proofStates = new ArrayList<ProofState>();
		for (int i = 0; i < names.length; i++) {
			proofStates.add(new ProofState(Utils.genSeq(names[i])));
		}
				
		hypChangedListeners = new HashSet<IHypothesisChangedListener>();
		goalChangedListeners = new HashSet<IGoalChangedListener>();
		poChangedListeners = new HashSet<IPOChangedListener>();
		proofTreeChangedListeners = new HashSet<IProofTreeChangedListener>();
		
		counter = -1;

		nextUndischargedPO();
		
	}
	
	private void nextUndischargedPO() {
		for (int i = 1; i < proofStates.size(); i++) {
			int index = (counter + i) % proofStates.size();
			ProofState ps = proofStates.get(index);
			if (!ps.isDischarged()) {
				// Calculate delta
				IHypothesisDelta hypDelta = calculateHypDelta(ps.getCurrentNode());
				IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(hypDelta);
				notifyHypothesisChangedListener(hypEvent);
				
				IGoalDelta goalDelta = new ProofTreeDelta(ps.getCurrentNode());
				IGoalChangeEvent goalEvent = new ProofTreeChangeEvent(goalDelta);
				notifyGoalChangedListener(goalEvent);
				
				IPODelta poDelta = new PODelta(ps.getProofTree());
				IPOChangeEvent poEvent = new POChangeEvent(poDelta);
				notifyPOChangedListener(poEvent);
				
				counter = index;
				proofState = ps;
				return;
			}
		}
		proofState = null;
	}
	
	private void prevUndischargedPO() {
		for (int i = 1; i < proofStates.size(); i++) {
			int index = (counter + proofStates.size() - i) % proofStates.size();
			ProofState ps = proofStates.get(index);
			if (!ps.isDischarged()) {
				// Calculate delta
				IHypothesisDelta hypDelta = calculateHypDelta(ps.getCurrentNode());
				IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(hypDelta);
				notifyHypothesisChangedListener(hypEvent);
				
				IGoalDelta goalDelta = new ProofTreeDelta(ps.getCurrentNode());
				IGoalChangeEvent goalEvent = new ProofTreeChangeEvent(goalDelta);
				notifyGoalChangedListener(goalEvent);
				
				IPODelta poDelta = new PODelta(ps.getProofTree());
				IPOChangeEvent poEvent = new POChangeEvent(poDelta);
				notifyPOChangedListener(poEvent);
				
				counter = index;
				proofState = ps;
				return;
			}
		}
		proofState = null;
	}
	
	public void addHypothesisChangedListener(IHypothesisChangedListener listener) {
		hypChangedListeners.add(listener);
	}
	
	public void removeHypothesisChangedListener(IHypothesisChangedListener listener) {
		hypChangedListeners.remove(listener);
	}
	
	private void notifyHypothesisChangedListener(IHypothesisChangeEvent e) {
		for (Iterator<IHypothesisChangedListener> i = hypChangedListeners.iterator(); i.hasNext();) {
			IHypothesisChangedListener listener = i.next();
			listener.hypothesisChanged(e);
		}
		return;
	}
	
	public void addGoalChangedListener(IGoalChangedListener listener) {
		goalChangedListeners.add(listener);
	}
	
	public void removeGoalChangedListener(IGoalChangedListener listener) {
		goalChangedListeners.remove(listener);
	}
	
	private void notifyGoalChangedListener(IGoalChangeEvent e) {
		for (Iterator<IGoalChangedListener> i = goalChangedListeners.iterator(); i.hasNext();) {
			IGoalChangedListener listener = i.next();
			listener.goalChanged(e);
		}
		return;
	}

	public void addPOChangedListener(IPOChangedListener listener) {
		poChangedListeners.add(listener);
	}
	
	public void removePOChangedListener(IPOChangedListener listener) {
		poChangedListeners.remove(listener);
	}
	
	private void notifyPOChangedListener(IPOChangeEvent e) {
		for (Iterator<IPOChangedListener> i = poChangedListeners.iterator(); i.hasNext();) {
			IPOChangedListener listener = i.next();
			listener.poChanged(e);
		}
		return;
	}

	public void addProofTreeChangedListener(IProofTreeChangedListener listener) {
		proofTreeChangedListeners.add(listener);
	}
	
	public void removeProofTreeChangedListener(IProofTreeChangedListener listener) {
		proofTreeChangedListeners.remove(listener);
	}
	
	private void notifyProofTreeChangedListener(IProofTreeChangeEvent e) {
		for (Iterator<IProofTreeChangedListener> i = proofTreeChangedListeners.iterator(); i.hasNext();) {
			IProofTreeChangedListener listener = i.next();
			listener.proofTreeChanged(e);
		}
		return;
	}

	public static List<Tactic> getApplicableToGoal(IProverSequent ps) {
		List<Tactic> result = new ArrayList<Tactic>();
		
		Rule rule;
		rule = new ConjI();
		if (rule.isApplicable(ps)) result.add(Tactics.conjI);
		
		rule = new ImpI();
		if (rule.isApplicable(ps)) result.add(Tactics.impI);
		
		rule = new Hyp();
		if (rule.isApplicable(ps)) result.add(Tactics.hyp);
		
		rule = new AllI();
		if (rule.isApplicable(ps)) result.add(Tactics.allI);
		
		return result;
	}

	public static List<Tactic> getApplicableToHypothesis(Hypothesis hyp) {
		List<Tactic> result = new ArrayList<Tactic>();
		result.add(Tactics.conjI);
		result.add(Tactics.impI);
		result.add(Tactics.hyp);
		return result;
	}


	public IHypothesisDelta calculateHypDelta(ProofTree newNode) {
		Collection<Hypothesis> newSelectedHypotheses;
		if (newNode == null) newSelectedHypotheses = new HashSet<Hypothesis>(); 
		else newSelectedHypotheses = newNode.getRootSeq().selectedHypotheses();
		Collection<Hypothesis> added = new HashSet<Hypothesis>();
		Collection<Hypothesis> removed = new HashSet<Hypothesis>();
		
		if (proofState != null && proofState.getCurrentNode() != null) {
			Collection<Hypothesis> currentSelectedHypotheses = proofState.getCurrentNode().getRootSeq().selectedHypotheses(); 
			for (Iterator<Hypothesis> it = currentSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!newSelectedHypotheses.contains(hp)) removed.add(hp); 
			}
		
			for (Iterator<Hypothesis> it = newSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!currentSelectedHypotheses.contains(hp)) added.add(hp);
			}
		}
		else {
			added = newSelectedHypotheses;
		}
		
		return new HypothesisDelta(added, removed);
	}
	
	
	public void selectNode(ProofTree pt) {
		IHypothesisDelta delta = calculateHypDelta(pt);
		IHypothesisChangeEvent e = new HypothesisChangeEvent(delta);
		notifyHypothesisChangedListener(e);
				
		if (!pt.equals(proofState.getCurrentNode())) {
			notifyGoalChangedListener(new ProofTreeChangeEvent(new ProofTreeDelta(pt)));
		}
				
		proofState.setCurrentNode(pt);
		return;
	}

	public ProofState getCurrentPO() {
		if (proofState != null)
			return proofState;
		return null;
	}
	
	public ProofState nextPO() {
		nextUndischargedPO();
		return(getCurrentPO());
	}
	
	public ProofState prevPO() {
		prevUndischargedPO();
		return(getCurrentPO());
	}

}
