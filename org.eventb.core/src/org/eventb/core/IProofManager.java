package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;

public interface IProofManager {

	public void makeFresh(IProgressMonitor monitor) throws RodinDBException;
	
	public IPSFile getPSFile();

	public IPSStatus[] getPSStatuses() throws RodinDBException;

	public IProofTree getProofTree(IPSStatus psStatus) throws RodinDBException;

	public void saveProofTree(IPSStatus status, IProofTree pt,
			IProgressMonitor monitor) throws CoreException;

	public void updateStatus(IPSStatus status, IProgressMonitor monitor) throws RodinDBException;
	
	public void makeConsistent() throws RodinDBException;
	
	public void save(IProgressMonitor monitor, boolean force) throws RodinDBException;
	
	public void clean() throws RodinDBException;
	
	public IPRProof getPRProof(String name);
	
	public IPSStatus getPSStatus(String name);
}
