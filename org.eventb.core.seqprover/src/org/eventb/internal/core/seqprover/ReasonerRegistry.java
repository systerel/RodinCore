/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved internal ReasonerInfo to external class ReasonerDesc
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.internal.core.seqprover.ReasonerDesc.makeUnknownReasonerDesc;
import static org.eventb.internal.core.seqprover.VersionedIdCodec.decodeId;
import static org.eventb.internal.core.seqprover.VersionedIdCodec.decodeVersion;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.ReasonerDesc.DummyReasoner;
import org.eventb.internal.core.seqprover.ReasonerDesc.ReasonerLoadingException;

/**
 * Singleton class implementing the reasoner registry.
 * Reasoner IDs with a version number are accepted as input parameters.
 * 
 * @see org.eventb.core.seqprover.IReasonerRegistry
 * 
 * @author Farhad Mehta
 */
public class ReasonerRegistry implements IReasonerRegistry {
	
	private static String REASONERS_ID =
		SequentProver.PLUGIN_ID + ".reasoners";

	private static final ReasonerRegistry SINGLETON_INSTANCE = new ReasonerRegistry();

	private static final String[] NO_STRING = new String[0];
	
	/**
	 * Debug flag for <code>REASONER_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String, ReasonerDesc> registry;
	
	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private ReasonerRegistry() {
		// Singleton implementation
	}
	
	public static ReasonerRegistry getReasonerRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	public synchronized boolean isRegistered(String id) {
		if (registry == null) {
			loadRegistry();
		}
		id = decodeId(id);
		return registry.containsKey(id);
	}
	
	public synchronized String[] getRegisteredIDs(){
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet().toArray(NO_STRING);
	}
	
	public IReasoner getReasonerInstance(String id){
		return getLiveReasonerDesc(id).getInstance();
	}
	
	public String getReasonerName(String id){
		return getLiveReasonerDesc(id).getName();
	}

	// version is considered serialized in the id
	// desired version may be NO_VERSION
	public synchronized ReasonerDesc getReasonerDesc(String id) {
		if (registry == null) {
			loadRegistry();
		}
		final String noVerId = decodeId(id);
		ReasonerDesc desc = registry.get(noVerId);
		if (desc == null) {
			// Unknown reasoner, just create a dummy entry
			desc = makeUnknownReasonerDesc(id);
			registry.put(noVerId, desc);
		} else {
			final int version = decodeVersion(id);
			if (version != desc.getVersion()) {
				 // same descriptor with version from id
				desc = desc.copyWithVersion(version);
			}
		}
		return desc;
	}
	
	public IReasonerDesc getReasonerDesc(String id, String signature) {
		final ReasonerDesc desc = getReasonerDesc(id);
		return desc.copyWithSignature(signature);
	}
	
	// version is considered borne by the reasoner instance
	public synchronized IReasonerDesc getLiveReasonerDesc(String id) {
		if (registry == null) {
			loadRegistry();
		}
		final String noVerId = decodeId(id);
		ReasonerDesc desc = registry.get(noVerId);
		if (desc == null) {
			// Unknown reasoner, just create a dummy entry
			desc = makeUnknownReasonerDesc(id);
			registry.put(noVerId, desc);
		}
		return desc;
	}
	
	/**
	 * Initializes the registry using extensions to the reasoner extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, ReasonerDesc>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(REASONERS_ID);
		for (IConfigurationElement element: xPoint.getConfigurationElements()) {
			try {
				final ReasonerDesc desc = new ReasonerDesc(element);
				final String id = desc.getId();
				final ReasonerDesc oldDesc = registry.put(id, desc);
				if (oldDesc != null) {
					registry.put(id, oldDesc);
					Util.log(null, "Duplicate reasoner extension " + id
							+ " ignored");
				} else {
					if (DEBUG)
						System.out.println("Registered reasoner extension "
								+ id);
				}
			} catch (ReasonerLoadingException e) {
				Util.log(e, "while loading reasoner registry");
			}
		}
	}
	
	public boolean isDummyReasoner(IReasoner reasoner){
		return reasoner instanceof DummyReasoner;
	}

}
