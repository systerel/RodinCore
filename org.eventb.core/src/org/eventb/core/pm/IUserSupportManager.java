package org.eventb.core.pm;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSFile;
import org.rodinp.core.RodinDBException;

public interface IUserSupportManager {

	public abstract IUserSupport newUserSupport();

	public abstract void disposeUserSupport(IUserSupport userSupport);

	public abstract Collection<IUserSupport> getUserSupports();

	public abstract void addUSManagerListener(IUSManagerListener listener);

	public abstract void removeUSManagerListener(IUSManagerListener listener);

	public abstract void setInput(IUserSupport userSupport, IPSFile prFile,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract IProvingMode getProvingMode();

}