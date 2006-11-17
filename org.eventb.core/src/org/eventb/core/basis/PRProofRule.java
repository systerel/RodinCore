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
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRRuleAntecedent;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofRule extends EventBProofElement implements IPRProofRule {

	public PRProofRule(String ruleID, IRodinElement parent) {
		super(ruleID, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}


	private String getReasonerID() throws RodinDBException {
		return getElementName();
	}

	private IReasoner getReasoner()throws RodinDBException {
		final IReasonerRegistry reasonerRegistry = SequentProver.getReasonerRegistry();
		return reasonerRegistry.getReasonerInstance(this.getReasonerID());
	} 

	private IReasonerInput getReasonerInput(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException {

		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;

		try {
			// TODO : check for getReasoner() == null
			IReasonerInputSerializer deserializer = 
				new ProofStoreReader.Bridge((PRReasonerInput)prReasonerInput[0],store,monitor);
			return (getReasoner()).deserializeInput(deserializer);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getNextedException();
		}
	}

	public IProofRule getProofRule(IProofStoreReader store, IProgressMonitor monitor)throws RodinDBException {

		Predicate goal = getGoal(store, monitor);		
		Set<Hypothesis> neededHyps = Hypothesis.Hypotheses(getHyps(store, monitor));

		IRodinElement[] rodinElements = this.getChildrenOfType(IPRRuleAntecedent.ELEMENT_TYPE);
		IAntecedent[] anticidents = new IAntecedent[rodinElements.length];
		for (int i = 0; i < rodinElements.length; i++) {
			anticidents[i] = ((IPRRuleAntecedent)rodinElements[i]).getAntecedent(store, monitor);
		}

		String display = getRuleDisplay(monitor);
		int confidence = getConfidence(monitor);


		return ProverFactory.makeProofRule(this.getReasoner(),this.getReasonerInput(store,monitor), goal, neededHyps, confidence, display, anticidents);
	}

	public void setProofRule(IProofRule proofRule, IProofStoreCollector store,IProgressMonitor monitor) throws RodinDBException {

		// write out the current goal and needed hypotheses		
		setGoal(proofRule.getGoal(), store, monitor);
		setHyps(Hypothesis.Predicates(proofRule.getNeededHyps()), store, monitor);

		// write out the anticidents (next subgoals)
		int idx = 1;
		for (IAntecedent antecedent : proofRule.getAntecedents()){
			((IPRRuleAntecedent)this.createInternalElement(
					IPRRuleAntecedent.ELEMENT_TYPE,
					"a" + idx++,null,null)).setAntecedent(antecedent,store,monitor);
		}

		// write out display
		setRuleDisplay(proofRule.getDisplayName(), monitor);

		// write out confidence level		
		setConfidence(proofRule.getConfidence(), monitor);

		// write out the reasoner input
		// TODO : remove this check
		if (proofRule.generatedUsing() != null)
		{
			IPRReasonerInput prReasonerInput =
				(IPRReasonerInput)
				this.createInternalElement(
						IPRReasonerInput.ELEMENT_TYPE,
						"",
						null,null);
			try {
				IReasonerInputSerializer serializer = 
					new ProofStoreCollector.Bridge(prReasonerInput,store,monitor); 
				proofRule.generatedUsing().serialize(serializer);
			} catch (SerializeException e) {
				throw (RodinDBException)e.getNextedException();
			}

		}

	}

	public String getRuleDisplay(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE, monitor);
	}

	public void setRuleDisplay(String display, IProgressMonitor monitor)
	throws RodinDBException {
		setAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE, display, monitor);
	}
}
