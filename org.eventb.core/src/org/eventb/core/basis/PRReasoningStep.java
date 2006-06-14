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
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerRegistry;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
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
	
	public SerializableReasonerInput getReasonerInput() throws RodinDBException {
		SerializableReasonerInput reasonerInput = new SerializableReasonerInput();
		IRodinElement[] prReasonerInput = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
		if (prReasonerInput.length == 0) return null;
		assert prReasonerInput.length == 1;
		IRodinElement[] properties = ((PRReasonerInput)prReasonerInput[0]).getChildrenOfType(IPair.ELEMENT_TYPE);
		for (IRodinElement property : properties)
			reasonerInput.putString(property.getElementName(),((IPair)property).getContents());
		
		IRodinElement[] predicates = ((PRReasonerInput)prReasonerInput[0]).getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		for (IRodinElement predicate : predicates)
			reasonerInput.putPredicate(predicate.getElementName(),((IPRPredicate)predicate).getPredicate());
		
		IRodinElement[] expressions = ((PRReasonerInput)prReasonerInput[0]).getChildrenOfType(IPRExpression.ELEMENT_TYPE);
		for (IRodinElement expression : expressions)
			reasonerInput.putExpression(expression.getElementName(),((IPRExpression)expression).getExpression());
		
		IRodinElement[] hypActions = ((PRReasonerInput)prReasonerInput[0]).getChildrenOfType(IPRHypAction.ELEMENT_TYPE);
		if (hypActions.length != 0)
		{
			assert hypActions.length == 1;
			reasonerInput.hypAction = ((IPRHypAction)hypActions[0]).getAction();
		}
		
		return reasonerInput;
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
		
		rodinElements = this.getChildrenOfType(IPair.ELEMENT_TYPE);
		assert rodinElements.length == 1;
		assert rodinElements[0].getElementName().equals("display");
		reasonerOutput.display = ((IPair)rodinElements[0]).getContents()+"*";
		
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
		for (Anticident anticident : reasonerOutput.anticidents){
			((IPRReasonerAnticident)this.createInternalElement(
					IPRReasonerAnticident.ELEMENT_TYPE,
					"anticident",null,null)).setAnticident(anticident);
		}
		
		// write out display
		((IPair)(this.createInternalElement(IPair.ELEMENT_TYPE,"display",null,null)))
		.setContents(reasonerOutput.display);
		
		// write out the reasoner input
		if (reasonerOutput.generatedUsing != null)
		{
			SerializableReasonerInput reasonerInput =
				reasonerOutput.generatedUsing.genSerializable();
			
			InternalElement prReasonerInput = 
				this.createInternalElement(
					PRReasonerInput.ELEMENT_TYPE,
					null,
					null,null);
			
			for (Map.Entry<String,String> pair : reasonerInput.properties.entrySet()) {
				prReasonerInput.createInternalElement(
						Pair.ELEMENT_TYPE,
						pair.getKey(),
						null,null)
						.setContents(pair.getValue());
			}
			for (Map.Entry<String,Predicate> pair : reasonerInput.predicates.entrySet()) {
				((IPRPredicate)(prReasonerInput.createInternalElement(
						IPRPredicate.ELEMENT_TYPE,
						pair.getKey(),
						null,null)))
						.setPredicate(pair.getValue());
			}
			
			for (Map.Entry<String,Expression> pair : reasonerInput.expressions.entrySet()) {
				((IPRExpression)(prReasonerInput.createInternalElement(
						IPRExpression.ELEMENT_TYPE,
						pair.getKey(),
						null,null)))
						.setExpression(pair.getValue());
			}
			if (reasonerInput.hypAction != null){
				((IPRHypAction)(prReasonerInput.createInternalElement(
						IPRHypAction.ELEMENT_TYPE,
						"hypAction",
						null,null)))
						.setAction(reasonerInput.hypAction);
			}
			
		}
		
	}
	
	
	
}
