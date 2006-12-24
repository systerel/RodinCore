package org.eventb.core.pm;

import java.util.Collection;

import org.rodinp.core.RodinDBException;

public interface IUserSupportManager {

	/**
	 * Create a new User Support with no input.
	 * <p>
	 * 
	 * @return a new instance of User Support
	 */
	public abstract IUserSupport newUserSupport();

	/**
	 * Dispose an existing User Support
	 * <p>
	 * 
	 * @param userSupport
	 *            an existing User Support to be disposed
	 */
//	public abstract void disposeUserSupport(IUserSupport userSupport);

	/**
	 * Set the input for an User Support. This is normally called right after
	 * creating a new User Support
	 * <p>
	 * 
	 * @param userSupport
	 *            an existing User Support
	 * @param psFile
	 *            the PSFile which will be the input for the User Support
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a Rodin Exception
	 */
//	public abstract void setInput(IUserSupport userSupport, IPSFile psFile,
//			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Return the list of User Support managed by the manager
	 * <p>
	 * 
	 * @return a collection of User Supports managed by the manager
	 */
	public abstract Collection<IUserSupport> getUserSupports();

	/**
	 * Add a listener to the manager
	 * <p>
	 * 
	 * @param listener
	 *            a USManager listener
	 */
	public abstract void addChangeListener(IUserSupportManagerChangedListener listener);

	/**
	 * Remove a listener from the manager
	 * <p>
	 * 
	 * @param listener
	 *            an existing USManager listener
	 */
	public abstract void removeChangeListener(IUserSupportManagerChangedListener listener);

	/**
	 * Return the proving mode
	 * <p>
	 * 
	 * @return the current proving mode
	 */
	public abstract IProvingMode getProvingMode();

	public void run(Runnable op) throws RodinDBException;

}