/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRReasonerAnticident;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPair;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofRule extends InternalElement implements IPRProofRule {

	public PRProofRule(String ruleID, IRodinElement parent) {
		super(ruleID, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	
	private String getReasonerID() throws RodinDBException {
		return getElementName();
	}
	
	private IReasoner getReasoner()throws RodinDBException {
		final IReasonerRegistry reasonerRegistry = SequentProver.getReasonerRegistry();
		IReasoner reasoner = reasonerRegistry.getReasonerInstance(this.getReasonerID());
		// Uninstalled reasoner
		if (reasoner == null) return null;
		return reasoner;
	} 
	
	private IReasonerInput getReasonerInput() throws RodinDBException {

		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;
		assert prReasonerInput.length == 1;
		
		try {
			// TODO : check for getReasoner() == null
			return (getReasoner()).deserializeInput((PRReasonerInput)prReasonerInput[0]);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getNextedException();
		}
	}
	
	public IProofRule getProofRule()throws RodinDBException {
		
		IRodinElement[] rodinElements = this.getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		assert rodinElements.length == 1;
		assert rodinElements[0].getElementName().equals("goal");
		Predicate goal = ((IPRPredicate)rodinElements[0]).getPredicate();
		
		rodinElements = this.getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		assert rodinElements.length == 1;
		assert rodinElements[0].getElementName().equals("neededHyps");
		Set<Hypothesis> neededHyps = Hypothesis.Hypotheses(((IPRPredicateSet)rodinElements[0]).getPredicateSet());
		
		rodinElements = this.getChildrenOfType(IPRReasonerAnticident.ELEMENT_TYPE);
		IAnticident[] anticidents = new IAnticident[rodinElements.length];
		for (int i = 0; i < rodinElements.length; i++) {
			anticidents[i] = ((IPRReasonerAnticident)rodinElements[i]).getAnticident();
		}
		
		String display = this.getInternalElement(IPair.ELEMENT_TYPE,"display").getContents();
		int confidence = Integer.parseInt(this.getInternalElement(IPair.ELEMENT_TYPE,"confidence").getContents());

		return RuleFactory.makeProofRule(this.getReasoner(),this.getReasonerInput(), goal, neededHyps, confidence, display, anticidents);
	}

	public void setProofRule(IProofRule proofRule) throws RodinDBException {
		// TODO : eventually delete cast
		// ProofRule proofRule = (ProofRule)rule;
		// delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the current goal and needed hypotheses
		((IPRPredicate)(this.createInternalElement(IPRPredicate.ELEMENT_TYPE,"goal",null,null)))
		.setPredicate(proofRule.getGoal());
		((PRPredicateSet)(this.createInternalElement(IPRPredicateSet.ELEMENT_TYPE,"neededHyps",null,null)))
		.setHypSet(proofRule.getNeededHyps());
		// write out the anticidents (next subgoals)
		int idx = 1;
		for (IAnticident anticident : proofRule.getAnticidents()){
			((IPRReasonerAnticident)this.createInternalElement(
					IPRReasonerAnticident.ELEMENT_TYPE,
					"anticident" + idx++,null,null)).setAnticident(anticident);
		}
		
		// write out display
		((IPair)(this.createInternalElement(IPair.ELEMENT_TYPE,"display",null,null)))
		.setContents(proofRule.getDisplayName());
		
		// write out confidence level
		((IPair)(this.createInternalElement(IPair.ELEMENT_TYPE,"confidence",null,null)))
		.setContents(Integer.toString(proofRule.getConfidence()));
		
		// write out the reasoner input
		// TODO : remove this check
		if (proofRule.generatedUsing() != null)
		{
			PRReasonerInput prReasonerInput =
				(PRReasonerInput)
				this.createInternalElement(
					PRReasonerInput.ELEMENT_TYPE,
					"",
					null,null);
			try {
				proofRule.generatedUsing().serialize(prReasonerInput);
			} catch (SerializeException e) {
				throw (RodinDBException)e.getNextedException();
			}
			
		}
		
	}

	
}
