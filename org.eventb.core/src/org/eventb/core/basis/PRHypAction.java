/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.seqprover.HypothesesManagement;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.HypothesesManagement.ActionType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */

public class PRHypAction extends EventBProofElement implements IPRHypAction {

	public PRHypAction(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public Action getAction(IProofStoreReader store) throws RodinDBException {
	
		// read in the action type
		ActionType actionType = HypothesesManagement.fromString(getElementName());
		
		// read in the hypotheses
		Set<Hypothesis> hyps = Hypothesis.Hypotheses(getHyps(store));
		return new Action(actionType,hyps);
	}

	public void setAction(Action a,  IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		
		// write out the hypotheses
		setHyps(Hypothesis.Predicates(a.getHyps()), store, monitor);
		
		return;
	}


}
