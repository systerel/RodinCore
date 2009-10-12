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

import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;

public class UserSupportManager implements IUserSupportManager {

	private Collection<IUserSupport> userSupports = new ArrayList<IUserSupport>();

	@Deprecated
	private static org.eventb.core.pm.IProvingMode provingMode;

	private static UserSupportManager instance;
	
	private DeltaProcessor deltaProcessor;

	private boolean considerHiddenHypotheses = false;
	
	private UserSupportManager() {
		// Singleton: Private default constructor
		deltaProcessor = new DeltaProcessor(this);
	}

	public static UserSupportManager getDefault() {
		if (instance == null)
			instance = new UserSupportManager();
		return instance;
	}

	public IUserSupport newUserSupport() {
		return new UserSupport();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#getUserSupports()
	 */
	public Collection<IUserSupport> getUserSupports() {
		return userSupports;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#addChangeListener(org.eventb.core.prover.IProofTreeChangedListener)
	 */
	public void addChangeListener(IUserSupportManagerChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#addChangeListener(org.eventb.core.prover.IProofTreeChangedListener)
	 */
	public void removeChangeListener(IUserSupportManagerChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#getProvingMode()
	 */
	@Deprecated
	public org.eventb.core.pm.IProvingMode getProvingMode() {
		if (provingMode == null)
			provingMode = new org.eventb.internal.core.pm.ProvingMode();
		return provingMode;
	}

	public DeltaProcessor getDeltaProcessor() {
		return deltaProcessor;
	}

	public void addUserSupport(UserSupport userSupport) {
		synchronized (userSupports) {
			if (!userSupports.contains(userSupport))
				userSupports.add(userSupport);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IUserSupportManager#disposeUserSupport(org.eventb.core.pm.IUserSupport)
	 */
	public void removeUserSupport(IUserSupport userSupport) {
		synchronized (userSupports) {
			if (userSupports.contains(userSupport))
				userSupports.remove(userSupport);
		}
	}

	public void run(Runnable op) {
		boolean wasEnable = deltaProcessor.isEnable();
		try {
			if (wasEnable)
				deltaProcessor.setEnable(false);
			op.run();
		}
		finally {
			if (wasEnable)
				deltaProcessor.setEnable(true);
		}
		deltaProcessor.fireDeltas();
	}

	public void setConsiderHiddenHypotheses(boolean value) {
		this.considerHiddenHypotheses  = value;
	}

	public boolean isConsiderHiddenHypotheses() {
		return considerHiddenHypotheses;
	}
}
