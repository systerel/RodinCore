/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
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

	public IHypAction getAction(IProofStoreReader store) throws RodinDBException {
	
		// read in the action type
		String actionType = getElementName();
		
		if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE))
			return ProverFactory.makeSelectHypAction(getHyps(store));
		if (actionType.equals(ISelectionHypAction.DESELECT_ACTION_TYPE))
			return ProverFactory.makeDeselectHypAction(getHyps(store));
		if (actionType.equals(ISelectionHypAction.HIDE_ACTION_TYPE))
			return ProverFactory.makeHideHypAction(getHyps(store));
		if (actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE))
			return ProverFactory.makeShowHypAction(getHyps(store));

		// TODO : forward inferences
		
		return null;
	}

	public void setAction(IHypAction a,  IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		
		String actionType = getElementName();

		if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE) || 
				actionType.equals(ISelectionHypAction.DESELECT_ACTION_TYPE) ||
				actionType.equals(ISelectionHypAction.HIDE_ACTION_TYPE) ||
				actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE))
			setHyps(((ISelectionHypAction)a).getHyps(), store, monitor);
//		 TODO : forward inferences
		return;
	}


}
