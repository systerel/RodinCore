/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IPSStatus;

/**
 * @author Maria Husmann
 *
 */
public class ModelPOContainer {
	// name for Label Provider
	public static final String DISPLAY_NAME = "Proof Obligations";
	
	protected HashMap<String, ModelProofObligation> proofObligations = new HashMap<String, ModelProofObligation>();

	public ModelProofObligation[] getProofObligations() {
		ModelProofObligation[] proofs = new ModelProofObligation[proofObligations.values().size()];
		return proofObligations.values().toArray(proofs);
	}
	
	public void addProofObligation(ModelProofObligation po){
		proofObligations.put(po.getElementName(), po);
	}

	public IPSStatus[] getIPSStatuses() {
		List<IPSStatus> statuses = new LinkedList<IPSStatus>();
		for (Iterator<ModelProofObligation> iterator = proofObligations.values().iterator(); iterator.hasNext();) {
			ModelProofObligation po = iterator.next();
			statuses.add(po.getIPSStatus());
		}
		IPSStatus[] results = new IPSStatus[statuses.size()];
		return statuses.toArray(results);
	}
	

}
