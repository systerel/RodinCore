/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.pm.IUSManagerListener;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.rodinp.core.RodinDBException;

public class UserSupportManager implements IUserSupportManager {

	private Collection<IUSManagerListener> listeners = new ArrayList<IUSManagerListener>();

	private Collection<IUserSupport> userSupports = new ArrayList<IUserSupport>();

	private static IProvingMode provingMode;

	private static IUserSupportManager instance;
	
	private UserSupportManager() {
		// Singleton: Private default constructor
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#newUserSupport()
	 */
	public IUserSupport newUserSupport() {
		IUserSupport userSupport = new UserSupport();
		userSupports.add(userSupport);
		notifyUSManagerListener(userSupport, IUSManagerListener.ADDED);
		return userSupport;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#disposeUserSupport(org.eventb.core.pm.IUserSupport)
	 */
	public void disposeUserSupport(IUserSupport userSupport) {
		userSupport.dispose();
		synchronized (userSupports) {
			if (userSupports.contains(userSupport))
				userSupports.remove(userSupport);
		}

		notifyUSManagerListener(userSupport, IUSManagerListener.REMOVED);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#getUserSupports()
	 */
	public Collection<IUserSupport> getUserSupports() {
		return userSupports;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#addUSManagerListener(org.eventb.core.pm.IUSManagerListener)
	 */
	public void addUSManagerListener(IUSManagerListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#removeUSManagerListener(org.eventb.core.pm.IUSManagerListener)
	 */
	public void removeUSManagerListener(IUSManagerListener listener) {
		synchronized (listeners) {
			if (listeners.contains(listener))
				listeners.remove(listener);
		}
	}

	private void notifyUSManagerListener(final IUserSupport userSupport,
			final int status) {
		IUSManagerListener[] safeCopy;
		synchronized (listeners) {
			safeCopy = listeners.toArray(new IUSManagerListener[listeners
					.size()]);
		}
		for (final IUSManagerListener listener : safeCopy) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// do nothing, will be logged by the platform
				}

				public void run() throws Exception {
					listener.USManagerChanged(userSupport, status);
				}
			});
		}

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#setInput(org.eventb.core.pm.IUserSupport, org.eventb.core.IPSFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setInput(IUserSupport userSupport, IPSFile prFile,
			IProgressMonitor monitor) throws RodinDBException {
		userSupport.setInput(prFile, monitor);
		notifyUSManagerListener(userSupport, IUSManagerListener.CHANGED);
	}

	
	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#getProvingMode()
	 */
	public IProvingMode getProvingMode() {
		if (provingMode == null)
			provingMode = new ProvingMode();
		return provingMode;
	}

	public static IUserSupportManager getDefault() {
		if (instance == null)
			instance = new UserSupportManager();
		return instance;
	}
}
