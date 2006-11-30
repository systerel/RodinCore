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
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPRRuleAntecedent;
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
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
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

	private IReasonerInput getReasonerInput(IProofStoreReader store) throws RodinDBException {

		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;

		try {
			// TODO : check for getReasoner() == null
			IReasonerInputSerializer deserializer = 
				new ProofStoreReader.Bridge((PRReasonerInput)prReasonerInput[0],store);
			return (getReasoner()).deserializeInput(deserializer);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getCause();
		}
	}

	public IProofSkeleton getProofSkeleton(IProofStoreReader store,
			final String comment) throws RodinDBException {

		final Predicate goal = getGoal(store);		
		final Set<Hypothesis> neededHyps = Hypothesis.Hypotheses(getHyps(store));

		final IPRRuleAntecedent[] prAntecedents = getAntecedents();
		final int length = prAntecedents.length;
		final IAntecedent[] anticidents = new IAntecedent[length];
		final IProofSkeleton[] children = new IProofSkeleton[length];
		for (int i = 0; i < length; i++) {
			anticidents[i] = prAntecedents[i].getAntecedent(store);
			children[i] = ((EventBProofElement) prAntecedents[i]).getSkeleton(store);
		}

		final String display = getRuleDisplay();
		final int confidence = getConfidence();

		final IProofRule proofRule = ProverFactory.makeProofRule(
				getReasoner(),
				getReasonerInput(store), 
				goal, 
				neededHyps, 
				confidence, 
				display, 
				anticidents);
		
		IProofSkeleton skeleton = new IProofSkeleton() {

				public IProofSkeleton[] getChildNodes() {
					return children;
				}

				public IProofRule getRule() {
					return proofRule;
				}

				public String getComment() {
					return comment;
				}
			
		};
		return skeleton;
	}

	public void setProofRule(IProofSkeleton skel, IProofStoreCollector store,IProgressMonitor monitor) throws RodinDBException {

		final IProofRule proofRule = skel.getRule();
		
		// write out the current goal and needed hypotheses		
		setGoal(proofRule.getGoal(), store, monitor);
		setHyps(Hypothesis.Predicates(proofRule.getNeededHyps()), store, monitor);

		// write out display
		setRuleDisplay(proofRule.getDisplayName(), monitor);

		// write out confidence level		
		setConfidence(proofRule.getConfidence(), monitor);

		// write out the reasoner input
		// TODO : remove this check
		if (proofRule.generatedUsing() != null)
		{
			IPRReasonerInput prReasonerInput = (IPRReasonerInput) getInternalElement(
					IPRReasonerInput.ELEMENT_TYPE, "");
			prReasonerInput.create(null, null);
			try {
				IReasonerInputSerializer serializer = 
					new ProofStoreCollector.Bridge(prReasonerInput,store,monitor); 
				proofRule.generatedUsing().serialize(serializer);
			} catch (SerializeException e) {
				throw (RodinDBException)e.getCause();
			}

		}

		// write out the anticidents (next subgoals)
		final IAntecedent[] antecedents = proofRule.getAntecedents();
		final IProofSkeleton[] children = skel.getChildNodes();
		assert antecedents.length == children.length;
		for (int i = 0; i < antecedents.length; i++) {
			PRRuleAntecedent child = (PRRuleAntecedent) getAntecedent(Integer.toString(i));
			child.create(null, null);
			child.setAntecedent(antecedents[i], store, monitor);
			child.setSkeleton(children[i], store, monitor);
		}
	}

	public String getRuleDisplay() throws RodinDBException {
		return getAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE);
	}

	public void setRuleDisplay(String display, IProgressMonitor monitor)
	throws RodinDBException {
		setAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE, display, monitor);
	}

	public IPRRuleAntecedent getAntecedent(String name) {
		return (IPRRuleAntecedent) getInternalElement(IPRRuleAntecedent.ELEMENT_TYPE, name);
	}

	public IPRRuleAntecedent[] getAntecedents() throws RodinDBException {
		return (IPRRuleAntecedent[]) getChildrenOfType(IPRRuleAntecedent.ELEMENT_TYPE);
	}

}
