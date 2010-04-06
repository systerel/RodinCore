/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deserialization of reasoner version through IReasonerDesc
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRRuleAntecedent;
import org.eventb.core.IPRStringInput;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public class PRProofRule extends EventBProofElement implements IPRProofRule {

	public PRProofRule(String ruleID, IRodinElement parent) {
		super(ruleID, parent);
	}

	@Override
	public IInternalElementType<IPRProofRule> getElementType() {
		return ELEMENT_TYPE;
	}


	private String getReasonerID() {
		return getElementName();
	}

	private IReasoner getReasoner() {
		return getReasonerDesc().getInstance();
	}
	
	// returns a descriptor with the version of the reasoner used in the proof
	// the version is encoded in the element name (this.getReasonerID())
	private IReasonerDesc getReasonerDesc() {
		final IReasonerRegistry reasonerRegistry = SequentProver.getReasonerRegistry();
		return reasonerRegistry.getReasonerDesc(this.getReasonerID());
	}

	public IProofSkeleton getProofSkeleton(IProofStoreReader store,
			final String comment) throws RodinDBException {

		Predicate goal = null;
		if (hasGoal()) goal = getGoal(store);		
		final Set<Predicate> neededHyps = getHyps(store);

		final IPRRuleAntecedent[] prAntecedents = getAntecedents();
		final int length = prAntecedents.length;
		final IAntecedent[] antecedents = new IAntecedent[length];
		final IProofSkeleton[] children = new IProofSkeleton[length];
		for (int i = 0; i < length; i++) {
			antecedents[i] = prAntecedents[i].getAntecedent(store);
			children[i] = ((EventBProofElement) prAntecedents[i]).getSkeleton(store);
		}

		final String display = getRuleDisplay();
		final int confidence = getConfidence();

		final IReasonerInput input;
		try {
			IReasonerInputReader deserializer = 
				new ProofStoreReader.Bridge(this, store, confidence,
						display, goal, neededHyps, antecedents);
				
			input = getReasoner().deserializeInput(deserializer);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getCause();
		}
		final IProofRule proofRule = ProverFactory.makeProofRule(
				getReasonerDesc(),
				input, 
				goal, 
				neededHyps, 
				confidence, 
				display, 
				antecedents);
		
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
		if (proofRule.getGoal()!= null) setGoal(proofRule.getGoal(), store, monitor);
		setHyps(proofRule.getNeededHyps(), store, monitor);

		// write out display
		setRuleDisplay(proofRule.getDisplayName(), monitor);

		// write out confidence level		
		setConfidence(proofRule.getConfidence(), monitor);

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

		// write out the reasoner input
		IReasoner reasoner = proofRule.generatedBy();
		IReasonerInput input = proofRule.generatedUsing();
		try {
			IReasonerInputWriter writer = 
				new ProofStoreCollector.Bridge(this, store, monitor); 
			reasoner.serializeInput(input, writer);
		} catch (SerializeException e) {
			// TODO check before casting
			throw (RodinDBException)e.getCause();
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
		return getInternalElement(IPRRuleAntecedent.ELEMENT_TYPE, name);
	}

	public IPRRuleAntecedent[] getAntecedents() throws RodinDBException {
		return getChildrenOfType(IPRRuleAntecedent.ELEMENT_TYPE);
	}

	public IPRExprRef getPRExprRef(String key) {
		return getInternalElement(IPRExprRef.ELEMENT_TYPE, "." + key);
	}

	public IPRPredRef getPRPredRef(String key) {
		return getInternalElement(IPRPredRef.ELEMENT_TYPE, "." + key);
	}

	public IPRStringInput getPRStringInput(String key) {
		return getInternalElement(IPRStringInput.ELEMENT_TYPE, "." + key);
	}

}
