/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRReasonerAnticident;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;
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
	public String getElementType() {
		return ELEMENT_TYPE;
	}
//
//	public ITypeEnvironment getTypeEnvironment() throws RodinDBException {
//		ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
//		for (IRodinElement pair : this.getChildrenOfType(IPair.ELEMENT_TYPE)) {
//			Type type = Lib.parseType(((IPair)pair).getContents());
//			assert type != null;
//			typEnv.addName(pair.getElementName(),type);
//		}
//		return typEnv;
//	}
//
//	public void setTypeEnvironment(ITypeEnvironment typeEnv) throws RodinDBException {
//		//	delete previous children, if any.
//		if (this.getChildren().length != 0)
//			this.getRodinDB().delete(this.getChildren(),true,null);
//		
//		// write out the type environment
//		Set<String> names = typeEnv.getNames();
//		
//		for (String name : names) {
//			this.createInternalElement(IPair.ELEMENT_TYPE,name,null,null)
//			.setContents(typeEnv.getType(name).toString());
//		}
//		
//	}

	public Anticident getAnticident() throws RodinDBException {
		Anticident anticident = new Anticident();
		IRodinElement[] children = 
			this.getChildrenOfType(IPRTypeEnvironment.ELEMENT_TYPE);
		if (children.length != 0)
		{
			assert children.length == 1;
			anticident.addedFreeIdentifiers = 
				((IPRTypeEnvironment)children[0]).getFreeIdentifiers();
		}
		children = this.getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		if (children.length != 0)
		{
			assert children.length == 1;
			anticident.addedHypotheses = 
				((IPRPredicateSet)children[0]).getPredicateSet();
		}
		children = this.getChildrenOfType(IPRHypAction.ELEMENT_TYPE);
		if (children.length != 0)
		{
			for (IRodinElement child : children) {
				anticident.hypAction.add( 
					((IPRHypAction)child).getAction());				
			}
		}
		
		children = this.getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		assert children.length == 1;
		anticident.subGoal = ((IPRPredicate)children[0]).getPredicate();
		
		return anticident;
	}

	public void setAnticident(Anticident anticident) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
	
		if (anticident.addedFreeIdentifiers.length != 0){
			((IPRTypeEnvironment)(this.createInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,
					"addedFreeIdentifiers",
					null,null))).setTypeEnvironment(anticident.addedFreeIdentifiers);
		}
		if (! anticident.addedHypotheses.isEmpty()){
			((IPRPredicateSet)(this.createInternalElement(IPRPredicateSet.ELEMENT_TYPE,
					"addedHyps",
					null,null))).setPredicateSet(anticident.addedHypotheses);
		}
		if (! anticident.hypAction.isEmpty()){
			for (Action action : anticident.hypAction) {
				((IPRHypAction)(this.createInternalElement(IPRHypAction.ELEMENT_TYPE,
						"hypAction",
						null,null))).setAction(action);
			}
		}
		
		((IPRPredicate)(this.createInternalElement(IPRPredicate.ELEMENT_TYPE,
				"subgoal",
				null,null))).setPredicate(anticident.subGoal);
		
	}

}
