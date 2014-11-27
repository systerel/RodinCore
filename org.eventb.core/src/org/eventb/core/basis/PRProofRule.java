/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deserialization of reasoner version through IReasonerDesc
 *     Systerel - used nested classes instead of anonymous ones
 *     Systerel - added broken input repair mechanism
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
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
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IRepairableInputReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.basis.ProofStoreCollector;
import org.eventb.internal.core.basis.ProofStoreReader;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PRProofRule extends EventBProofElement implements IPRProofRule {

	private static class ProofSkeleton extends EmptySkeleton {
	
		private final IProofSkeleton[] children;
		
		private final IProofRule proofRule;
	
		public ProofSkeleton(IProofSkeleton[] children, String comment,
				IProofRule proofRule) {
			super(comment);
			this.children = children;
			this.proofRule = proofRule;
		}
	
		@Override
		public IProofSkeleton[] getChildNodes() {
			return children;
		}
	
		@Override
		public IProofRule getRule() {
			return proofRule;
		}
	}

	public PRProofRule(String ruleID, IRodinElement parent) {
		super(ruleID, parent);
	}

	@Override
	public IInternalElementType<IPRProofRule> getElementType() {
		return ELEMENT_TYPE;
	}

	private String getReasonerRef() {
		return getElementName();
	}

	// returns a descriptor with the version of the reasoner used in the proof
	private IReasonerDesc getReasonerDesc(IProofStoreReader store) throws RodinDBException {
		return store.getReasoner(this.getReasonerRef());
	}

	@Override
	public IProofSkeleton getProofSkeleton(IProofStoreReader store,
			final String comment) throws CoreException {

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
		final IReasonerDesc reasonerDesc = getReasonerDesc(store);
		final int confidence = reasonerDesc.isTrusted() ? getConfidence()
				: IConfidence.UNCERTAIN_MAX;
				

		final IReasonerInputReader deserializer = new ProofStoreReader.Bridge(
				this, store, confidence, display, goal, neededHyps, antecedents);

		final IReasonerInput input;
		try {
			input = getInput(reasonerDesc, deserializer);
		} catch (Exception e) {
			final String msg = "while deserializing rule of reasoner: "
							+ reasonerDesc.getId();
			Util.log(e, msg);
			return new EmptySkeleton(comment);
		}
		
		final IProofRule proofRule = ProverFactory.makeProofRule(
				reasonerDesc,
				input, 
				goal, 
				neededHyps, 
				confidence, 
				display, 
				antecedents);
		
		final IProofSkeleton skeleton = new ProofSkeleton(children, comment,
				proofRule);
		return skeleton;
	}

	private static IReasonerInput getInput(IReasonerDesc reasonerDesc,
			IReasonerInputReader deserializer) throws RodinDBException {
		final IReasoner reasoner = reasonerDesc.getInstance();
		try {
			return reasoner.deserializeInput(deserializer);
		} catch (SerializeException e) {
			if (reasoner instanceof IRepairableInputReasoner) {
				IReasonerInput repaired = null;
				try {
					repaired = ((IRepairableInputReasoner) reasoner)
							.repair(deserializer);
				} catch (Exception e1) {
					final String msg1 = "while repairing input of reasoner: "
							+ reasoner.getReasonerID();
					Util.log(e1, msg1);
					throw Util.newRodinDBException(msg1, e1);				}
				if (repaired != null) {
					return repaired;
				}
			}
			final Throwable cause = e.getCause();
			final String msg = "while deserializing input of reasoner: "
					+ reasoner.getReasonerID();
			Util.log(cause, msg);
			throw Util.newRodinDBException(msg, cause);
		}
	}

	@Override
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
			PRRuleAntecedent child = (PRRuleAntecedent) createChild(
					IPRRuleAntecedent.ELEMENT_TYPE, null, monitor);
			child.setAntecedent(antecedents[i], store, monitor);
			child.setSkeleton(children[i], store, monitor);
		}

		// write out the reasoner input
		final IReasoner reasoner = proofRule.generatedBy();
		final IReasonerInput input = proofRule.generatedUsing();
		final IReasonerInputWriter writer =
			new ProofStoreCollector.Bridge(this, store, monitor);
		try {
			reasoner.serializeInput(input, writer);
		} catch (SerializeException e) {
			final Throwable cause = e.getCause();
			final String msg = "while serializing input of reasoner: "
					+ reasoner.getReasonerID();
			throw Util.newRodinDBException(msg, cause);
		}
	}

	public String getRuleDisplay() throws RodinDBException {
		return getAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE);
	}

	public void setRuleDisplay(String display, IProgressMonitor monitor)
	throws RodinDBException {
		setAttributeValue(EventBAttributes.RULE_DISPLAY_ATTRIBUTE, display, monitor);
	}

	@Override
	public IPRRuleAntecedent getAntecedent(String name) {
		return getInternalElement(IPRRuleAntecedent.ELEMENT_TYPE, name);
	}

	@Override
	public IPRRuleAntecedent[] getAntecedents() throws RodinDBException {
		return getChildrenOfType(IPRRuleAntecedent.ELEMENT_TYPE);
	}

	@Override
	public IPRExprRef getPRExprRef(String key) {
		return getInternalElement(IPRExprRef.ELEMENT_TYPE, "." + key);
	}

	@Override
	public IPRPredRef getPRPredRef(String key) {
		return getInternalElement(IPRPredRef.ELEMENT_TYPE, "." + key);
	}

	@Override
	public IPRStringInput getPRStringInput(String key) {
		return getInternalElement(IPRStringInput.ELEMENT_TYPE, "." + key);
	}

}
