package org.eventb.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.rules.AllI;
import org.eventb.core.prover.rules.ConjI;
import org.eventb.core.prover.rules.Hyp;
import org.eventb.core.prover.rules.IProofRule;
import org.eventb.core.prover.rules.ImpI;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;
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
			IProofTree tree = SequentProver.makeProofTree(Utils.genSeq(names[i]));
			proofStates.add(new ProofState(tree));
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
		
		IProofRule rule;
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


	public IHypothesisDelta calculateHypDelta(IProofTreeNode newNode) {
		Collection<Hypothesis> newSelectedHypotheses;
		if (newNode == null) newSelectedHypotheses = new HashSet<Hypothesis>(); 
		else newSelectedHypotheses = newNode.getSequent().selectedHypotheses();

		Collection<Hypothesis> addedToSelected = new HashSet<Hypothesis>();
		Collection<Hypothesis> removedFromSelected = new HashSet<Hypothesis>();
		
		if (proofState != null && proofState.getCurrentNode() != null) {
			Collection<Hypothesis> currentSelectedHypotheses = proofState.getCurrentNode().getSequent().selectedHypotheses(); 
			for (Iterator<Hypothesis> it = currentSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!newSelectedHypotheses.contains(hp)) removedFromSelected.add(hp); 
			}
		
			for (Iterator<Hypothesis> it = newSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!currentSelectedHypotheses.contains(hp)) addedToSelected.add(hp);
			}
		}
		else {
			addedToSelected = newSelectedHypotheses;
		}
		
		Collection<Hypothesis> addedToCached = new HashSet<Hypothesis>();
		Collection<Hypothesis> removedFromCached = new HashSet<Hypothesis>();
		if (proofState != null) {
			Collection<Hypothesis> newDisplayCached = new HashSet<Hypothesis>();
			Collection<Hypothesis> currentDisplayCached = proofState.getDisplayCached();
			for (Iterator<Hypothesis> it = proofState.getCached().iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
//				System.out.print("In cache: " + hyp);
				if (currentDisplayCached.contains(hyp)) {   
//					System.out.print(", currently display");
					if (isValid(hyp, newNode) && !isSelected(hyp, newNode)) { // cached, display, valid & not selected
//						System.out.println(", valid");
						newDisplayCached.add(hyp);
					}
					else {                                  // cached, display, (invalid or selected)
//						System.out.println(", invalid");
						removedFromCached.add(hyp);
					}
					currentDisplayCached.remove(hyp);
				}
				else {
//					System.out.print(", not currently display");
					if (isValid(hyp, newNode) && !isSelected(hyp, newNode)) { // cached, not(display), valid & not selected
//						System.out.println(", valid");
						newDisplayCached.add(hyp);
						addedToCached.add(hyp);
					}
				}
			}
			
			for (Iterator<Hypothesis> it = currentDisplayCached.iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
//				System.out.println("Currently display but not in cached: " + hyp);
				removedFromCached.add(hyp);                        // display, invalid or selected, not(cached)
			}
	
			proofState.setDisplayCached(newDisplayCached);
		}
		
		Collection<Hypothesis> addedToSearched = new HashSet<Hypothesis>();
		Collection<Hypothesis> removedFromSearched = new HashSet<Hypothesis>();
		if (proofState != null) {
			Collection<Hypothesis> newDisplaySearched = new HashSet<Hypothesis>();
			Collection<Hypothesis> currentDisplaySearched = proofState.getDisplaySearched();
			for (Iterator<Hypothesis> it = proofState.getSearched().iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
//				System.out.print("In cache: " + hyp);
				if (currentDisplaySearched.contains(hyp)) {   
//					System.out.print(", currently display");
					if (isValid(hyp, newNode) && !isSelected(hyp, newNode)) { // cached, display, valid & not selected
//						System.out.println(", valid");
						newDisplaySearched.add(hyp);
					}
					else {                                  // cached, display, (invalid or selected)
//						System.out.println(", invalid");
						removedFromSearched.add(hyp);
					}
					currentDisplaySearched.remove(hyp);
				}
				else {
//					System.out.print(", not currently display");
					if (isValid(hyp, newNode) && !isSelected(hyp, newNode)) { // cached, not(display), valid & not selected
//						System.out.println(", valid");
						newDisplaySearched.add(hyp);
						addedToSearched.add(hyp);
					}
				}
			}
			
			for (Iterator<Hypothesis> it = currentDisplaySearched.iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
//				System.out.println("Currently display but not in cached: " + hyp);
				removedFromSearched.add(hyp);                        // display, invalid or selected, not(cached)
			}
	
			proofState.setDisplaySearched(newDisplaySearched);
		}
		
		return new HypothesisDelta(addedToSelected, removedFromSelected, addedToCached, removedFromCached, addedToSearched, removedFromSearched);
	}
	
	private boolean isValid(Hypothesis hyp, IProofTreeNode pt) {
		return (pt != null && pt.getSequent().hypotheses().contains(hyp));
	}
	
	private boolean isSelected(Hypothesis hyp, IProofTreeNode pt) {
		return pt.getSequent().selectedHypotheses().contains(hyp);
	}
	
	public void selectNode(IProofTreeNode pt) {
		IHypothesisDelta delta = calculateHypDelta(pt);
		IHypothesisChangeEvent e = new HypothesisChangeEvent(delta);
		notifyHypothesisChangedListener(e);
				
		if (pt == null || (!pt.equals(proofState.getCurrentNode()))) {
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

	public void prune(IProofTreeNode pt) {
		Tactics.prune.apply(pt);
		notifyGoalChangedListener(new ProofTreeChangeEvent(new ProofTreeDelta(pt)));
		// Generate Delta
	}

//	public void deselectHypothesis(Set<Hypothesis> hyps) {
//		Tactic t = Tactics.mngHyp(HypothesesManagement.ActionType.DESELECT, hyps);
//		t.apply(proofState.getCurrentNode());
//		proofState.addAllToCached(hyps);
//	}
//	
	public void manageHypotheses(ActionType action, Set<Hypothesis> hyps) {
		Tactic t = Tactics.mngHyp(action, hyps);
		t.apply(proofState.getCurrentNode());
		proofState.addAllToCached(hyps);
	}
	
	public void removeHypotheses(int origin, Collection<Hypothesis> hyps) {
		IHypothesisDelta delta;
		IHypothesisChangeEvent e;
		switch (origin) {
		case (IHypothesisDelta.CACHED):
			Collection<Hypothesis> removedFromCached = new HashSet<Hypothesis>();
			for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
				if (proofState.getDisplayCached().contains(hyp))
					removedFromCached.add(hyp);
			}
			delta = new HypothesisDelta(null, null, null, removedFromCached, null, null);
			e = new HypothesisChangeEvent(delta);
			notifyHypothesisChangedListener(e);
			proofState.removeAllFromCached(hyps);
			break;
		case (IHypothesisDelta.SEARCHED):
			Collection<Hypothesis> removedFromSearched = new HashSet<Hypothesis>();
			for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
				Hypothesis hyp = it.next();
				if (proofState.getDisplayCached().contains(hyp))
					removedFromSearched.add(hyp);
			}
			delta = new HypothesisDelta(null, null, null, null, null, removedFromSearched);
			e = new HypothesisChangeEvent(delta);
			notifyHypothesisChangedListener(e);
			proofState.removeAllFromCached(hyps);
		}
			
	}
	
}
