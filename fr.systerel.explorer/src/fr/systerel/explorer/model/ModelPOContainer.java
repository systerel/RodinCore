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
 * An Interface for any elements that may contain ProofObligations
 *
 */
public class ModelPOContainer implements IModelElement{
	
	// name for Label Provider
	public static final String DISPLAY_NAME = "Proof Obligations";
	
	protected IModelElement parent;
	
	protected HashMap<String, ModelProofObligation> proofObligations = new HashMap<String, ModelProofObligation>();

	public ModelProofObligation[] getProofObligations() {
		ModelProofObligation[] proofs = new ModelProofObligation[proofObligations.values().size()];
		return proofObligations.values().toArray(proofs);
	}
	
	public void addProofObligation(ModelProofObligation po){
		proofObligations.put(po.getElementName(), po);
	}

	/**
	 * 
	 * @return 	The IPSStatuses of the ProofObligations in this container 
	 * 			It is possible that some ProofObligatiosn don't have a status
	 */
	public IPSStatus[] getIPSStatuses() {
		List<IPSStatus> statuses = new LinkedList<IPSStatus>();
		for (Iterator<ModelProofObligation> iterator = proofObligations.values().iterator(); iterator.hasNext();) {
			ModelProofObligation po = iterator.next();
			if (po.getIPSStatus() != null) {
				statuses.add(po.getIPSStatus());
			}
		}
		IPSStatus[] results = new IPSStatus[statuses.size()];
		return statuses.toArray(results);
	}

	public IModelElement getParent() {
		return parent;
	}
	
	public boolean hasUndischargedPOs(){
		for (Iterator<ModelProofObligation> iterator = proofObligations.values().iterator(); iterator.hasNext();) {
			ModelProofObligation po = iterator.next();
			if (!po.isDischarged()) {
				return true;
			}
			
		}
		
		return false;
	}
	

}
