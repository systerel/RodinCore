package org.eventb.core.pm;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.rodinp.core.RodinDBException;

public interface IProofState {

	public abstract void loadProofTree(IProgressMonitor monitor)
			throws RodinDBException;

	public abstract boolean isClosed() throws RodinDBException;

	public abstract IPSStatus getPRSequent();

	public abstract IProofTree getProofTree();

	public abstract IProofTreeNode getCurrentNode();

	public abstract void setCurrentNode(IProofTreeNode newNode);

	public abstract IProofTreeNode getNextPendingSubgoal(IProofTreeNode node);

	public abstract IProofTreeNode getNextPendingSubgoal();

	public abstract void addAllToCached(Collection<Predicate> hyps);

	public abstract void removeAllFromCached(Collection<Predicate> hyps);

	public abstract Collection<Predicate> getCached();

	public abstract void removeAllFromSearched(Collection<Predicate> hyps);

	public abstract Collection<Predicate> getSearched();

	public abstract void setSearched(Collection<Predicate> searched);

	public abstract boolean isDirty();

	public abstract void doSave(IProgressMonitor monitor) throws CoreException;

	public abstract void setDirty(boolean dirty);

	public abstract boolean equals(Object obj);

	// Pre: Must be initalised and not currently saving.
	public abstract void proofReuse(IProofMonitor monitor)
			throws RodinDBException;

	public abstract boolean isUninitialised();

	public abstract boolean isSequentDischarged() throws RodinDBException;

	public abstract boolean isProofReusable() throws RodinDBException;

	public abstract void reloadProofTree() throws RodinDBException;

	public abstract void unloadProofTree();

}