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

	/**
	 * Run the give action as an atomic User Support Manager operation.
	 * <p>
	 * After running a method that changes user supportss,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an user support manager changed delta.
	 * This method allows clients to call a number of
	 * methods that changes user supports and only have user support manager
	 * changed notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * user support manager changed notification describing the net effect of all changes
	 * done to user supports by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 * @param op the operation to perform
	 * @throws RodinDBException if the operation failed.
	 */
	public void run(Runnable op) throws RodinDBException;

}