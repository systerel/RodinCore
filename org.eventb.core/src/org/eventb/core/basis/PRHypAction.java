/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 * @since 1.0
 *
 */

public class PRHypAction extends EventBProofElement implements IPRHypAction {

	public PRHypAction(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRHypAction> getElementType() {
		return ELEMENT_TYPE;
	}

	public IHypAction getAction(IProofStoreReader store) throws RodinDBException {
	
		// read in the action type
		String actionType = getElementName();
		
		if (actionType.startsWith(ISelectionHypAction.SELECT_ACTION_TYPE))
			return ProverFactory.makeSelectHypAction(getHyps(store));
		if (actionType.startsWith(ISelectionHypAction.DESELECT_ACTION_TYPE))
			return ProverFactory.makeDeselectHypAction(getHyps(store));
		if (actionType.startsWith(ISelectionHypAction.HIDE_ACTION_TYPE))
			return ProverFactory.makeHideHypAction(getHyps(store));
		if (actionType.startsWith(ISelectionHypAction.SHOW_ACTION_TYPE))
			return ProverFactory.makeShowHypAction(getHyps(store));

		if (actionType.startsWith(IForwardInfHypAction.ACTION_TYPE)){
			Collection<Predicate> hyps = getHyps(store);
			FreeIdentifier[] addedFreeIdents = getFreeIdents(store.getFormulaFactory());
			Collection<Predicate> infHyps = getInfHyps(store);
			return ProverFactory.makeForwardInfHypAction(hyps,addedFreeIdents, infHyps);
		}
		
		return null;
	}

	public void setAction(IHypAction a,  IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		
		String actionType = a.getActionType();

		if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE) || 
				actionType.equals(ISelectionHypAction.DESELECT_ACTION_TYPE) ||
				actionType.equals(ISelectionHypAction.HIDE_ACTION_TYPE) ||
				actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE)) {
			setHyps(((ISelectionHypAction)a).getHyps(), store, monitor);
		} else if (actionType.equals(IForwardInfHypAction.ACTION_TYPE)) {
			final IForwardInfHypAction forwardInf = ((IForwardInfHypAction)a);
			setHyps(forwardInf.getHyps(), store, monitor);
			setFreeIdents(forwardInf.getAddedFreeIdents(), monitor);
			setInfHyps(forwardInf.getInferredHyps(), store, monitor);
		}
	}


}
