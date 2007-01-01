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