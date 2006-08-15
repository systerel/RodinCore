/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Map;

import org.eventb.core.IPRExpression;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRReasonerAnticident;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPRReasoningStep;
import org.eventb.core.IPair;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerRegistry;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.sequent.Hypothesis;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRReasoningStep extends InternalElement implements IPRReasoningStep {

	public PRReasoningStep(String reasonerID, IRodinElement parent) {
		super(reasonerID, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public String getReasonerID() throws RodinDBException {
		return getElementName();
	}
	
	public Reasoner getReasoner()throws RodinDBException {
		Reasoner reasoner = ReasonerRegistry.getReasoner(this.getReasonerID());
		// Uninstalled reasoner
		if (reasoner == null) return null;
		return reasoner;
	} 
	
	public ReasonerInput getReasonerInput() throws RodinDBException {

		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;
		assert prReasonerInput.length == 1;
		
		// if (getReasoner() instanceof ConjE)
		try {
			return (getReasoner()).deserializeInput((PRReasonerInput)prReasonerInput[0]);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getNextedException();
		}
		// return ((PRReasonerInput)prReasonerInput[0]).getReasonerInput();
	}
	
	public ReasonerInput getReasonerInput1() throws RodinDBException {

		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;
		assert prReasonerInput.length == 1;
		
		try {
			return ((ConjE)getReasoner()).deserializeInput((PRReasonerInput)prReasonerInput[0]);
		} catch (SerializeException e) {
			throw (RodinDBException) e.getNextedException();
		}
	}
	
	public ReasonerOutputSucc getReasonerOutput()throws RodinDBException {
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this.getReasoner(),this.getReasonerInput());
		
		IRodinElement[] rodinElements = this.getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		assert rodinElements.length == 1;
		assert rodinElements[0].getElementName().equals("goal");
		reasonerOutput.goal = ((IPRPredicate)rodinElements[0]).getPredicate();
		
		rodinElements = this.getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		assert rodinElements.length == 1;
		assert rodinElements[0].getElementName().equals("neededHypotheses");
		reasonerOutput.neededHypotheses = Hypothesis.Hypotheses(((IPRPredicateSet)rodinElements[0]).getPredicateSet());
		
		rodinElements = this.getChildrenOfType(IPRReasonerAnticident.ELEMENT_TYPE);
		reasonerOutput.anticidents = new Anticident[rodinElements.length];
		for (int i = 0; i < rodinElements.length; i++) {
			reasonerOutput.anticidents[i] = ((IPRReasonerAnticident)rodinElements[i]).getAnticident();
		}
		
		reasonerOutput.display = 
			this.getInternalElement(IPair.ELEMENT_TYPE,"display").getContents();
		reasonerOutput.reasonerConfidence = 
			Integer.parseInt(this.getInternalElement(IPair.ELEMENT_TYPE,"confidence").getContents());

//		rodinElements = this.getChildrenOfType(IPair.ELEMENT_TYPE);
//		assert rodinElements.length == 1;
//		assert rodinElements[0].getElementName().equals("display");
//		reasonerOutput.display = ((IPair)rodinElements[0]).getContents()+"*";
	
		return reasonerOutput;
	}

	public void setReasonerOutput(ReasonerOutputSucc reasonerOutput) throws RodinDBException {
		// delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the current goal and needed hypotheses
		((IPRPredicate)(this.createInternalElement(IPRPredicate.ELEMENT_TYPE,"goal",null,null)))
		.setPredicate(reasonerOutput.goal);
		((PRPredicateSet)(this.createInternalElement(IPRPredicateSet.ELEMENT_TYPE,"neededHypotheses",null,null)))
		.setHypSet(reasonerOutput.neededHypotheses);
		// write out the anticidents (next subgoals)
		int idx = 1;
		for (Anticident anticident : reasonerOutput.anticidents){
			((IPRReasonerAnticident)this.createInternalElement(
					IPRReasonerAnticident.ELEMENT_TYPE,
					"anticident" + idx++,null,null)).setAnticident(anticident);
		}
		
		// write out display
		((IPair)(this.createInternalElement(IPair.ELEMENT_TYPE,"display",null,null)))
		.setContents(reasonerOutput.display);
		
		// write out confidence level
		((IPair)(this.createInternalElement(IPair.ELEMENT_TYPE,"confidence",null,null)))
		.setContents(Integer.toString(reasonerOutput.reasonerConfidence));
		
		// write out the reasoner input
		if (reasonerOutput.generatedUsing != null)
		{
//			SerializableReasonerInput reasonerInput =
//				reasonerOutput.generatedUsing.genSerializable();
//			
//			PRReasonerInput prReasonerInput =
//				(PRReasonerInput)
//				this.createInternalElement(
//					PRReasonerInput.ELEMENT_TYPE,
//					"",
//					null,null);
//			
//			prReasonerInput.setReasonerInput(reasonerInput);
			
			PRReasonerInput prReasonerInput =
				(PRReasonerInput)
				this.createInternalElement(
					PRReasonerInput.ELEMENT_TYPE,
					"",
					null,null);
			try {
				reasonerOutput.generatedUsing.serialize(prReasonerInput);
			} catch (SerializeException e) {
				throw (RodinDBException)e.getNextedException();
			}
			
		}
		
	}

	
	
	
	
}
