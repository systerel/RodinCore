/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRReasonerAnticident;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRReasonerAnticident extends InternalElement implements IPRReasonerAnticident {

	public PRReasonerAnticident(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	private InternalElement getChild(IInternalElementType childType,
			String childName) throws RodinDBException {

		InternalElement internalElement = 
			getInternalElement(childType,childName);
		assert internalElement != null;
		if (internalElement.exists()) return internalElement;
		throw newNotPresentException();
	}
	
	public IAnticident getAnticident() throws RodinDBException {
		
		InternalElement child; 
		
		child = getChild(IPRPredicate.ELEMENT_TYPE,"goal");
		Predicate goal = ((IPRPredicate)child).getPredicate();
		
		// optional entries
		FreeIdentifier[] addedFreeIdens = null;
		Set<Predicate> addedHyps = null;
		List<Action> hypAction = null;
		
		child = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"addedFreeIdents");
		if (child.exists()) addedFreeIdens = ((IPRTypeEnvironment)child).getFreeIdentifiers();
		
		child = getInternalElement(IPRPredicateSet.ELEMENT_TYPE,"addedHyps");
		if (child.exists()) addedHyps = ((IPRPredicateSet)child).getPredicateSet();
		
		IRodinElement[] children = getChildrenOfType(IPRHypAction.ELEMENT_TYPE);
		if (children.length != 0)
		{
			hypAction = new ArrayList<Action>(children.length);
			for (IRodinElement action : children) {
				hypAction.add(((IPRHypAction)action).getAction());				
			}
		}
		
		return ProverFactory.makeAnticident(goal,addedHyps,addedFreeIdens,hypAction);
	}

	public void setAnticident(IAnticident anticident) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
	
		if (anticident.getAddedFreeIdents().length != 0){
			((IPRTypeEnvironment)(this.createInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,
					"addedFreeIdents",
					null,null))).setTypeEnvironment(anticident.getAddedFreeIdents());
		}
		if (! anticident.getAddedHyps().isEmpty()){
			((IPRPredicateSet)(this.createInternalElement(IPRPredicateSet.ELEMENT_TYPE,
					"addedHyps",
					null,null))).setPredicateSet(anticident.getAddedHyps());
		}
		if (! anticident.getHypAction().isEmpty()){
			int count = 0;
			for (Action action : anticident.getHypAction()) {
				((IPRHypAction)(this.createInternalElement(IPRHypAction.ELEMENT_TYPE,
						"hypAction"+count,
						null,null))).setAction(action);
				count ++;
			}
		}
		
		((IPRPredicate)(this.createInternalElement(IPRPredicate.ELEMENT_TYPE,
				"goal",
				null,null))).setPredicate(anticident.getGoal());
		
	}

}
