package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;


// Rename to IPSPR
public interface IPSWrapper {

	public IPSFile getPSFile();
	
	public IPSStatus[] getPSStatuses() throws RodinDBException;

	public IProofTree getFreshProofTree(IPSStatus psStatus) throws RodinDBException;

	public IProofSkeleton getProofSkeleton(IPSStatus psStatus, IProgressMonitor monitor) throws RodinDBException;
	
	public void setProofTree(IPSStatus status, IProofTree pt,
			IProgressMonitor monitor) throws CoreException;

	public void save(IProgressMonitor monitor, boolean force) throws RodinDBException;
	
}
