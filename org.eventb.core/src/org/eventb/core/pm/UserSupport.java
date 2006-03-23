package org.eventb.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.core.pm.GoalChangeEvent;
import org.eventb.internal.core.pm.GoalDelta;
import org.eventb.internal.core.pm.HypothesisChangeEvent;
import org.eventb.internal.core.pm.HypothesisDelta;
import org.eventb.internal.core.pm.POChangeEvent;
import org.eventb.internal.core.pm.PODelta;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class UserSupport
	implements IElementChangedListener
{
	
	// TODO UserSupport needs to listen to the Database
	
	Collection<IHypothesisChangedListener> hypChangedListeners;
	Collection<IGoalChangedListener> goalChangedListeners;
	Collection<IPOChangedListener> poChangedListeners;
	Collection<IProofStatusChangedListener> proofStatusChangedListeners;
	Collection<IStatusChangedListener> statusChangedListeners;
	
	private List<ProofState> proofStates;
	private int counter;
	private ProofState currentPS;
	private IPRFile prFile;
	
	public UserSupport() {
		hypChangedListeners = new HashSet<IHypothesisChangedListener>();
		goalChangedListeners = new HashSet<IGoalChangedListener>();
		poChangedListeners = new HashSet<IPOChangedListener>();
		proofStatusChangedListeners = new HashSet<IProofStatusChangedListener>();
		statusChangedListeners = new HashSet<IStatusChangedListener>();
		
		proofStates = new ArrayList<ProofState>();
		RodinCore.addElementChangedListener(this);
	}

	
	/**
	 * This method return the current Obligation (Proof State).
	 * This should be called at the initialisation of a listener of the
	 * UserSupport.
	 * After that the listeners will update their states by listen to the
	 * changes from the UserSupport
	 * @return the current ProofState (can be null).
	 */
	public ProofState getCurrentPO() {
		return currentPS;
	}
	
	public void setInput(IPRFile prFile) throws RodinDBException {
		this.prFile = prFile;
		proofStates = new ArrayList<ProofState>();
		try {
			for (int i = 0; i < prFile.getSequents().length; i++) {
				IPRSequent prSequent = (IPRSequent) prFile.getSequents()[i];
				proofStates.add(new ProofState(prSequent));
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		counter = -1;
		nextUndischargedPO();
	}
	
	
	public void setCurrentPO(IPRSequent prSequent) throws RodinDBException {
		for (int i = 1; i <= proofStates.size(); i++) {
			int index = (counter + i) % proofStates.size();
			ProofState ps = proofStates.get(index);
			if (ps.getPRSequent().equals(prSequent)) {
				setProofState(ps, index);
				notifyStatusChangedListener(null);
				return;
			}
		}
	}
	
	
	public void nextUndischargedPO() throws RodinDBException {
		for (int i = 1; i <= proofStates.size(); i++) {
			int index = (counter + i) % proofStates.size();
			ProofState ps = proofStates.get(index);
			if (!ps.isDischarged()) {
				setProofState(ps, index);
				notifyStatusChangedListener(null);
				return;
			}
		}
//		currentPS = null;
		notifyStatusChangedListener("No undischarged PO found");
	}
	
	public void prevUndischargedPO() throws RodinDBException {
		for (int i = 1; i < proofStates.size(); i++) {
			int index = (counter + proofStates.size() - i) % proofStates.size();
			ProofState ps = proofStates.get(index);
			if (!ps.isDischarged()) {
				setProofState(ps, index);
				notifyStatusChangedListener(null);
				return;
			}
		}
//		currentPS = null;
		notifyStatusChangedListener("No undischarged PO found");
	}
	
	
	private void setProofState(ProofState ps, int index) throws RodinDBException {
		
		// Calculate delta
		ps.createProofTree();
		IProofTreeNode newCurrentNode = ps.getCurrentNode();
		if (newCurrentNode == null) newCurrentNode = ps.getNextPendingSubgoal();
				
		Collection<IHypothesisDelta> hypDelta = calculateHypDelta(ps, newCurrentNode);
		IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, hypDelta);
		
		IGoalDelta goalDelta = new GoalDelta(newCurrentNode);
		IGoalChangeEvent goalEvent = new GoalChangeEvent(goalDelta);
		
		IPODelta poDelta = new PODelta(ps);
		IPOChangeEvent poEvent = new POChangeEvent(poDelta);
		
		counter = index;
		currentPS = ps;
		currentPS.setCurrentNode(newCurrentNode);
		notifyHypothesisChangedListener(hypEvent);
		notifyGoalChangedListener(goalEvent);
		notifyPOChangedListener(poEvent);

		return;
	}
	
	private Collection<IHypothesisDelta> calculateHypDelta(
			ProofState newProofState, 
			IProofTreeNode newNode) {
		
		IProofTreeNode currentNode = null;
		if (currentPS != null) {
			currentNode = currentPS.getCurrentNode();
		}

		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		
		Collection<Hypothesis> newSelected;
		if (newNode == null) newSelected = new HashSet<Hypothesis>(); 
		else newSelected = newNode.getSequent().selectedHypotheses();
		Collection<Hypothesis> currentSelected;
		if (currentNode == null) currentSelected = new HashSet<Hypothesis>();
		else currentSelected = currentNode.getSequent().selectedHypotheses();
		for (Iterator<Hypothesis> it = currentSelected.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!newSelected.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSelected();
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newSelected.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!currentSelected.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSelected();
				delta.add(d);
			}
		}
		
		Collection<Hypothesis> newCached;
		if (newProofState == null) newCached = new HashSet<Hypothesis>(); 
		else newCached = newProofState.getCached();
		Collection<Hypothesis> currentCached;
		if (currentPS == null) currentCached = new HashSet<Hypothesis>();
		else currentCached = currentPS.getCached(); 
		for (Iterator<Hypothesis> it = currentCached.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!isValid(hp, newNode) || !newCached.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromCached();
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newCached.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (isValid(hp, newNode) && !currentCached.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToCached();
				delta.add(d);
			}
		}

		Collection<Hypothesis> newSearched;
		if (newProofState == null) newSearched = new HashSet<Hypothesis>(); 
		else newSearched = newProofState.getSearched();
		Collection<Hypothesis> currentSearched;
		if (currentPS == null) currentSearched = new HashSet<Hypothesis>(); 
		else currentSearched = currentPS.getSearched();
		for (Iterator<Hypothesis> it = currentSearched.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!isValid(hp, newNode) || !newSearched.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSearched();
				delta.add(d);
			}
		}
		for (Iterator<Hypothesis> it = newSearched.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (isValid(hp, newNode) && !currentSearched.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSearched();
				delta.add(d);
			}
		}
				
		return delta;
	}
	
	private boolean isValid(Hypothesis hyp, IProofTreeNode pt) {
//		UserSupportUtils.debug("Is Valid? " + (pt != null && pt.getSequent().hypotheses().contains(hyp)));
		return (pt != null && pt.getSequent().hypotheses().contains(hyp));
	}
	
	
	/**
	 * This is the response of the UserSupport for selecting a node in the current
	 * Proof Tree.
	 */ 
	public void selectNode(IProofTreeNode pt) {
		Collection<IHypothesisDelta> delta = calculateHypDelta(currentPS, pt);
		IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(e);
				
		if (pt != null) {
			notifyGoalChangedListener(new GoalChangeEvent(new GoalDelta(pt)));
		}
		
		if (currentPS != null) currentPS.setCurrentNode(pt);
		
		return;
	}


	public void applyTacticToHypotheses(ITactic t, Set<Hypothesis> hyps) throws RodinDBException {
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		for (Hypothesis hyp : hyps) {
			if (!currentPS.getCached().contains(hyp)) {
				HypothesisDelta d = new HypothesisDelta(hyp);
				d.setAddedToCached();
				delta.add(d);
			}
		}
		currentPS.addAllToCached(hyps);
		IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(e);
		applyTactic(t);
	}
	
	public void applyTactic(ITactic t) throws RodinDBException {
		IProofTreeNode currentNode = currentPS.getCurrentNode();
		Object information = t.apply(currentNode);
		Tactics.postProcess().apply(currentNode);
		if (information == null) {
			currentPS.updateStatus();
			
			notifyProofStatusChangedListener(currentPS.isDischarged());
			
			IProofTreeNode newNode = currentPS.getNextPendingSubgoal(currentNode);
			if (newNode == null) newNode = currentNode;
			
			Collection<IHypothesisDelta> hypDelta = calculateHypDelta(currentPS, newNode);
			IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, hypDelta);
			notifyHypothesisChangedListener(hypEvent);
			
			notifyGoalChangedListener(new GoalChangeEvent(new GoalDelta(newNode)));
			currentPS.setCurrentNode(newNode);
			
			IPODelta poDelta = new PODelta(currentPS);
			IPOChangeEvent poEvent = new POChangeEvent(poDelta);
			notifyPOChangedListener(poEvent);
			notifyStatusChangedListener("Tactic applied successfully");
		}
		else {
			currentPS.updateStatus();
			
			notifyProofStatusChangedListener(currentPS.isDischarged());
			
			IProofTreeNode newNode = currentPS.getNextPendingSubgoal(currentNode);
			if (newNode == null) newNode = currentNode;
			
			Collection<IHypothesisDelta> hypDelta = calculateHypDelta(currentPS, newNode);
			IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, hypDelta);
			notifyHypothesisChangedListener(hypEvent);
			
			notifyGoalChangedListener(new GoalChangeEvent(new GoalDelta(newNode)));
			currentPS.setCurrentNode(newNode);
			
			IPODelta poDelta = new PODelta(currentPS);
			IPOChangeEvent poEvent = new POChangeEvent(poDelta);
			notifyPOChangedListener(poEvent);
			notifyStatusChangedListener(information);
		}
	}


	
	public void removeCachedHypotheses(Collection<Hypothesis> hyps) {
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
			Hypothesis hyp = it.next();
			HypothesisDelta d = new HypothesisDelta(hyp);
			d.setRemovedFromCached();
			delta.add(d);
		}
		IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(e);
		currentPS.removeAllFromCached(hyps);
		notifyStatusChangedListener("Hypotheses removed from cached");
		return;
	}

	public void removeSearchedHypotheses(Collection<Hypothesis> hyps) {
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
			Hypothesis hyp = it.next();
			HypothesisDelta d = new HypothesisDelta(hyp);
			d.setRemovedFromSearched();
			delta.add(d);
		}
		IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(e);
		currentPS.removeAllFromSearched(hyps);
		notifyStatusChangedListener("Hypotheses removed from searched");
	}


	public void searchHyps(String token) {
		Set<Hypothesis> hyps = Hypothesis.textSearch(currentPS.getCurrentNode().getSequent().hypotheses(), token);
		
		Collection<Hypothesis> currentHyps = currentPS.getSearched();
		
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		
		for (Iterator<Hypothesis> it = currentHyps.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!isValid(hp, currentPS.getCurrentNode()) || !hyps.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSearched();
				delta.add(d);
			}
		}
	
		for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (isValid(hp, currentPS.getCurrentNode()) && !currentHyps.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSearched();
				delta.add(d);
			}
		}

		currentPS.setSearched(hyps);
		IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(hypEvent);
	}

	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
//		UserSupportUtils.debug("Element changed");
		try {
			processDelta(event.getDelta());
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	private void processDelta(IRodinElementDelta delta) throws RodinDBException {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
//		UserSupportUtils.debug("Process Delta " + element);
		if (element instanceof IRodinProject) {
//			UserSupportUtils.debug("Project changed " + kind + " for " + ((IRodinProject) element).getElementName());
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
		}
		else if (element instanceof IPRFile) {
//			UserSupportUtils.debug("PRFile changed " + kind + " for " + ((IPRFile) element).getElementName());
			if (prFile.equals(element)) {
//				setInput((IPRFile) element);
				for (IRodinElementDelta d : delta.getAffectedChildren()) {
					processDelta(d);
				}
			}
		}
		else if (element instanceof IPRSequent) {
//			UserSupportUtils.debug("IPRSequent changed " + kind + " for " + ((IPRSequent) element).getElementName());
			Collection<ProofState> remove = new HashSet<ProofState>();
			for (ProofState ps : proofStates) {
				if (ps.getPRSequent().equals(element)) {
					if (kind == IRodinElementDelta.ADDED) {
						UserSupportUtils.debug("Updated " + ((IPRSequent) element).getElementName());
						ps.initProofTree();
						if (ps == currentPS) setCurrentPO(ps.getPRSequent());
					}
					else if (kind == IRodinElementDelta.REMOVED) {
						UserSupportUtils.debug("Removed " + ((IPRSequent) element).getElementName());
						remove.add(ps);
					}
					else { // CHANGED
						UserSupportUtils.debug("Changed " + ((IPRSequent) element).getElementName());
					}
				}
			}
			proofStates.removeAll(remove);
		}
		else if (element instanceof IMachine) {
			return;
		}
		else if (element instanceof IContext) {
			return;
		}
		else if (element instanceof IParent) {
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
		}
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

	public void addProofStatusChangedListener(IProofStatusChangedListener listener) {
		proofStatusChangedListeners.add(listener);
	}
	
	public void removeProofStatusChangedListener(IProofStatusChangedListener listener) {
		proofStatusChangedListeners.remove(listener);
	}

	private void notifyProofStatusChangedListener(boolean complete) {
		for (Iterator<IProofStatusChangedListener> i = proofStatusChangedListeners.iterator(); i.hasNext();) {
			IProofStatusChangedListener listener = i.next();
			listener.proofStatusChanged(complete);
		}
		return;
	}
	
	public void addStatusChangedListener(IStatusChangedListener listener) {
		statusChangedListeners.add(listener);
	}
	
	public void removeStatusChangedListener(IStatusChangedListener listener) {
		statusChangedListeners.remove(listener);
	}

	private void notifyStatusChangedListener(Object information) {
		for (Iterator<IStatusChangedListener> i = statusChangedListeners.iterator(); i.hasNext();) {
			IStatusChangedListener listener = i.next();
			listener.statusChanged(information);
		}
		return;
	}


	public void back() throws RodinDBException {
		if (currentPS.getCurrentNode().getParent() != null) {
			selectNode(currentPS.getCurrentNode().getParent());
			applyTactic(Tactics.prune());
		}
	}
	
}