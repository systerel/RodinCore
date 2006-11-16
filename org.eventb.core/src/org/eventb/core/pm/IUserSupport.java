package org.eventb.core.pm;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSstatus;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.RodinDBException;

public interface IUserSupport {

	public abstract void addStateChangedListeners(
			IProofStateChangedListener listener);

	public abstract void removeStateChangedListeners(
			IProofStateChangedListener listener);

	public abstract void notifyStateChangedListeners();

	public abstract void fireProofStateDelta(IProofStateDelta newDelta);

	public abstract void batchOperation(Runnable op);

	/**
	 * This method return the current Obligation (Proof State). This should be
	 * called at the initialisation of a listener of the UserSupport. After that
	 * the listeners will update their states by listen to the changes from the
	 * UserSupport
	 * 
	 * @return the current ProofState (can be null).
	 */
	public abstract IProofState getCurrentPO();

	// Should be called by the UserSupportManager?
	public abstract void setInput(IPSFile prFile, IProgressMonitor monitor)
			throws RodinDBException;

	public abstract void setCurrentPO(IPSstatus prSequent,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract void nextUndischargedPO(boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract void prevUndischargedPO(boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * This is the response of the UserSupport for selecting a node in the
	 * current Proof Tree.
	 */
	public abstract void selectNode(IProofTreeNode pt);

	public abstract void applyTacticToHypotheses(final ITactic t,
			final Set<Hypothesis> hyps, final IProgressMonitor monitor);

	public abstract void applyTactic(final ITactic t,
			final IProgressMonitor monitor);

	public abstract void prune(final IProgressMonitor monitor)
			throws RodinDBException;

	public abstract void removeCachedHypotheses(Collection<Hypothesis> hyps);

	public abstract void removeSearchedHypotheses(Collection<Hypothesis> hyps);

	public abstract void searchHyps(String token);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public abstract void elementChanged(final ElementChangedEvent event);

	public abstract void back(IProgressMonitor monitor) throws RodinDBException;

	public abstract boolean hasUnsavedChanges();

	public abstract IProofState[] getUnsavedPOs();

	public abstract void proofTreeChanged(IProofTreeDelta proofTreeDelta);

	public abstract void setComment(String text, IProofTreeNode currentNode);

	public abstract Collection<IProofState> getPOs();

	public abstract IPSFile getInput();

	// Should be used by the UserSupportManager only
	public abstract void dispose();

}