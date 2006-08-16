/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPair;
import org.eventb.core.seqprover.sequent.HypothesesManagement;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.HypothesesManagement.Action;
import org.eventb.core.seqprover.sequent.HypothesesManagement.ActionType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */

public class PRHypAction extends InternalElement implements IPRHypAction {

	public PRHypAction(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public Action getAction() throws RodinDBException {
	
		// read in the action type
		IRodinElement[] pairs = this.getChildrenOfType(IPair.ELEMENT_TYPE);
		assert pairs.length == 1;
		ActionType actionType = HypothesesManagement.fromString(((IPair)pairs[0]).getContents());
		
		// read in the hypotheses
		IRodinElement[] predicateSets = this.getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		assert predicateSets.length == 1;
		Set<Hypothesis> hyps = Hypothesis.Hypotheses(((IPRPredicateSet)predicateSets[0]).getPredicateSet());
		
		return new Action(actionType,hyps);
	}

	public void setAction(Action a) throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the action type
		this.createInternalElement(IPair.ELEMENT_TYPE,"ActionType",null,null)
		.setContents(a.getType().toString());
		
		// write out the hypotheses
		((IPRPredicateSet)this.createInternalElement(IPRPredicateSet.ELEMENT_TYPE,"",null,null))
		.setPredicateSet(Hypothesis.Predicates(a.getHyps()));
		
		return;
	}


}
