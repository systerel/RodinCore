/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEventBFile;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;

/**
 * Unique implementation of {@link IProofManager} as a singleton class.
 * <p>
 * This class must be thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ProofManager implements IProofManager {

	private static final ProofManager instance = new ProofManager();

	public static ProofManager getDefault() {
		return instance;
	}

	/**
	 * Map of known proof components. All proof components that have been
	 * created by this manager are recorded here through a soft reference (so
	 * that they can get garbage collected when memory is needed).
	 * <p>
	 * All accesses to this field must be synchronized (locking of this object).
	 * </p>
	 */
	private final Map<IPSFile, Reference<ProofComponent>> known;

	private ProofManager() {
		// singleton constructor to be called once.
		known = new HashMap<IPSFile, Reference<ProofComponent>>();
	}

	public synchronized IProofAttempt[] getProofAttempts() {
		final List<ProofAttempt> res = new ArrayList<ProofAttempt>();
		for (final Reference<ProofComponent> ref: known.values()) {
			final ProofComponent pc = ref.get();
			if (pc != null) {
				pc.addAllAttempts(res);
			}
		}
		return res.toArray(ProofComponent.NO_PROOF_ATTEMPTS);
	}

	public IProofComponent getProofComponent(IEventBFile file) {
		final IPSFile psFile = file.getPSFile();
		return internalGet(psFile);
	}

	private synchronized IProofComponent internalGet(IPSFile psFile) {
		final Reference<ProofComponent> ref = known.get(psFile);
		if (ref != null) {
			final IProofComponent res = ref.get();
			if (res != null) {
				return res;
			}
		}
		final ProofComponent res = new ProofComponent(psFile);
		known.put(psFile, new SoftReference<ProofComponent>(res));
		return res;
	}

}
