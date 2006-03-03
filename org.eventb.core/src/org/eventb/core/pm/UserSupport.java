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
	private ProofState proofState;
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

	
	public void setInput(IPRFile prFile) throws RodinDBException {
		this.prFile = prFile;
		proofStates = new ArrayList<ProofState>();
		try {
			for (int i = 0; i < prFile.getSequents().length; i++) {
				IPRSequent prSequent = prFile.getSequents()[i];
				proofStates.add(new ProofState(prSequent));
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		counter = -1;
		nextUndischargedPO();
		notifyStatusChangedListener(null);
	}
	
	public void setCurrentPO(IPRSequent prSequent) {
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
		proofState = null;
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
		proofState = null;
		notifyStatusChangedListener("No undischarged PO found");
	}
	
	private void setProofState(ProofState ps, int index) {
		// Calculate delta
		if (ps.getCurrentNode() == null) ps.setCurrentNode(ps.getNextPendingSubgoal());
		
		IProofTreeNode currentNode = ps.getCurrentNode();
		Collection<IHypothesisDelta> hypDelta = calculateHypDelta(ps, currentNode);
		IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, hypDelta);
		notifyHypothesisChangedListener(hypEvent);
		
		IGoalDelta goalDelta = new GoalDelta(currentNode);
		IGoalChangeEvent goalEvent = new GoalChangeEvent(goalDelta);
		notifyGoalChangedListener(goalEvent);
		
		IPODelta poDelta = new PODelta(ps);
		IPOChangeEvent poEvent = new POChangeEvent(poDelta);
		notifyPOChangedListener(poEvent);
		
		counter = index;
		proofState = ps;
		
		return;
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
	
	private Collection<IHypothesisDelta> calculateHypDelta(ProofState newProofState, IProofTreeNode newNode) {
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		
		Collection<Hypothesis> newSelectedHypotheses;
		if (newNode == null) newSelectedHypotheses = new HashSet<Hypothesis>(); 
		else newSelectedHypotheses = newNode.getSequent().selectedHypotheses();

		if (proofState != null && proofState.getCurrentNode() != null) {
			Collection<Hypothesis> currentSelectedHypotheses = proofState.getCurrentNode().getSequent().selectedHypotheses(); 
			for (Iterator<Hypothesis> it = currentSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!newSelectedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setRemovedFromSelected();
					delta.add(d);
				}
			}
		
			for (Iterator<Hypothesis> it = newSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!currentSelectedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setAddedToSelected();
					delta.add(d);
				}
			}
		}
		else {
			for (Iterator<Hypothesis> it = newSelectedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSelected();
				delta.add(d);
			}
		}
		
		Collection<Hypothesis> newCachedHypotheses;
		if (newProofState == null) newCachedHypotheses = new HashSet<Hypothesis>(); 
		else newCachedHypotheses = newProofState.getCached();

		if (proofState != null) {
			Collection<Hypothesis> currentCachedHypotheses = proofState.getCached(); 
			for (Iterator<Hypothesis> it = currentCachedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!isValid(hp, newNode) || !newCachedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setRemovedFromCached();
					delta.add(d);
				}
			}
		
			for (Iterator<Hypothesis> it = newCachedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (isValid(hp, newNode) && !currentCachedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setAddedToCached();
					delta.add(d);
				}
			}
		}
		else {
			for (Iterator<Hypothesis> it = newCachedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (isValid(hp, newNode)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setAddedToCached();
					delta.add(d);
				}
			}
		}

		Collection<Hypothesis> newSearchedHypotheses;
		if (newProofState == null) newSearchedHypotheses = new HashSet<Hypothesis>(); 
		else newSearchedHypotheses = newProofState.getSearched();

		if (proofState != null) {
			Collection<Hypothesis> currentSearchedHypotheses = proofState.getSearched(); 
			for (Iterator<Hypothesis> it = currentSearchedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (!isValid(hp, newNode) || !newSearchedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setRemovedFromSearched();
					delta.add(d);
				}
			}
		
			for (Iterator<Hypothesis> it = newSearchedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (isValid(hp, newNode) && !currentSearchedHypotheses.contains(hp)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setAddedToSearched();
					delta.add(d);
				}
			}
		}
		else {
			for (Iterator<Hypothesis> it = newSearchedHypotheses.iterator(); it.hasNext();) {
				Hypothesis hp = it.next();
				if (isValid(hp, newNode)) {
					HypothesisDelta d = new HypothesisDelta(hp);
					d.setAddedToSearched();
					delta.add(d);
				}
			}
		}
				
		return delta;
	}
	
	private boolean isValid(Hypothesis hyp, IProofTreeNode pt) {
		UserSupportUtils.debug("Is Valid? " + (pt != null && pt.getSequent().hypotheses().contains(hyp)));
		return (pt != null && pt.getSequent().hypotheses().contains(hyp));
	}
	
	
	/**
	 * This is the response of the UserSupport for selecting a node in the current
	 * Proof Tree.
	 */ 
	public void selectNode(IProofTreeNode pt) {
		Collection<IHypothesisDelta> delta = calculateHypDelta(proofState, pt);
		IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(e);
				
		if (pt != null) {
			notifyGoalChangedListener(new GoalChangeEvent(new GoalDelta(pt)));
		}
		
		if (proofState != null) proofState.setCurrentNode(pt);
//		notifyStatusChangedListener("");
		return;
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
		return proofState;
	}

	public void applyTacticToHypotheses(ITactic t, Set<Hypothesis> hyps) throws RodinDBException {
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		for (Hypothesis hyp : hyps) {
			if (!proofState.getCached().contains(hyp)) {
				HypothesisDelta d = new HypothesisDelta(hyp);
				d.setAddedToCached();
				delta.add(d);
				IHypothesisChangeEvent e = new HypothesisChangeEvent(this, delta);
				notifyHypothesisChangedListener(e);
			}
		}
		proofState.addAllToCached(hyps);
		applyTactic(t);
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
		proofState.removeAllFromCached(hyps);
		notifyStatusChangedListener("Hypotheses removed");
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
		proofState.removeAllFromSearched(hyps);
	}


	public void applyTactic(ITactic t) throws RodinDBException {
		IProofTreeNode currentNode = proofState.getCurrentNode();
		Object information = t.apply(currentNode);
		Tactics.postProcess().apply(currentNode);
		if (information == null) {
			proofState.updateStatus();
			
			notifyProofStatusChangedListener(proofState.isDischarged());
			
			IProofTreeNode newNode = proofState.getNextPendingSubgoal(currentNode);
			if (newNode == null) newNode = currentNode;
			
			Collection<IHypothesisDelta> hypDelta = calculateHypDelta(proofState, newNode);
			IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, hypDelta);
			notifyHypothesisChangedListener(hypEvent);
			
			notifyGoalChangedListener(new GoalChangeEvent(new GoalDelta(newNode)));
			proofState.setCurrentNode(newNode);
			
			IPODelta poDelta = new PODelta(proofState);
			IPOChangeEvent poEvent = new POChangeEvent(poDelta);
			notifyPOChangedListener(poEvent);
			notifyStatusChangedListener("Tactic applied successfully");
		}
		else {
			notifyStatusChangedListener(information);
		}
	}

	public void searchHyps(String token) {
		Set<Hypothesis> hyps = Hypothesis.textSearch(proofState.getCurrentNode().getSequent().hypotheses(), token);
		
		Collection<Hypothesis> currentHyps = proofState.getSearched();
		Collection<IHypothesisDelta> delta = new HashSet<IHypothesisDelta>();
		
		for (Iterator<Hypothesis> it = currentHyps.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (!isValid(hp, proofState.getCurrentNode()) || !hyps.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setRemovedFromSearched();
				delta.add(d);
			}
		}
	
		for (Iterator<Hypothesis> it = hyps.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			if (isValid(hp, proofState.getCurrentNode()) && !currentHyps.contains(hp)) {
				HypothesisDelta d = new HypothesisDelta(hp);
				d.setAddedToSearched();
				delta.add(d);
			}
		}

		proofState.setSearched(hyps);
		IHypothesisChangeEvent hypEvent = new HypothesisChangeEvent(this, delta);
		notifyHypothesisChangedListener(hypEvent);

	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		UserSupportUtils.debug("Element changed");
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
//			UserSupportUtils.debug("Project changed " + kind + " for " + element);
			for (IRodinElementDelta d : delta.getAffectedChildren()) {
				processDelta(d);
			}
		}
		else if (element instanceof IPRFile) {
			UserSupportUtils.debug("PRFile changed " + kind + " for " + ((IPRFile) element).getElementName());
			if (prFile.equals(element)) {
				setInput((IPRFile) element);
//				for (IRodinElementDelta d : delta.getAffectedChildren()) {
//					processDelta(d);
//				}
			}
		}
		else if (element instanceof IPRSequent) {
			UserSupportUtils.debug("IPRSequent changed " + kind + " for " + ((IPRSequent) element).getElementName());
			for (ProofState ps : proofStates) {
				if (ps.getPRSequent().equals(element)) {
					UserSupportUtils.debug("Update " + ((IPRSequent) element).getElementName());
					ps.createProofTree();
				}
			}
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
	
}