/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Arrays;
import java.util.Map;

import org.eventb.core.IPRExpression;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPair;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRReasonerInput extends InternalElement implements IPRReasonerInput {

	public PRReasonerInput(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

//	public SerializableReasonerInput getReasonerInput() throws RodinDBException {
//		SerializableReasonerInput reasonerInput = new SerializableReasonerInput();
//		
//		IRodinElement[] properties = (getChildrenOfType(IPair.ELEMENT_TYPE));
//		for (IRodinElement property : properties)
//			reasonerInput.putString(property.getElementName(),((IPair)property).getContents());
//		
//		IRodinElement[] predicates = (getChildrenOfType(IPRPredicate.ELEMENT_TYPE));
//		for (IRodinElement predicate : predicates)
//			reasonerInput.putPredicate(predicate.getElementName(),((IPRPredicate)predicate).getPredicate());
//		
//		IRodinElement[] expressions = (getChildrenOfType(IPRExpression.ELEMENT_TYPE));
//		for (IRodinElement expression : expressions)
//			reasonerInput.putExpression(expression.getElementName(),((IPRExpression)expression).getExpression());
//		
//		IRodinElement[] hypActions = (getChildrenOfType(IPRHypAction.ELEMENT_TYPE));
//		if (hypActions.length != 0)
//		{
//			assert hypActions.length == 1;
//			reasonerInput.hypAction = ((IPRHypAction)hypActions[0]).getAction();
//		}
//		
//		IRodinElement[] children = (getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE));
//		if (children.length != 0)
//		{
//			SerializableReasonerInput[] SRIchildren = new SerializableReasonerInput[children.length];
//			for (int i = 0; i < children.length; i++) {
//				SRIchildren[i] = ((PRReasonerInput)children[i]).getReasonerInput();
//			}
//			reasonerInput.children = SRIchildren;
//		}
//				
//		return reasonerInput;
//	}

	
//	public void setReasonerInput(SerializableReasonerInput reasonerInput) throws RodinDBException {
//		
//		PRReasonerInput prReasonerInput = this;
//		
//		for (Map.Entry<String,String> pair : reasonerInput.properties.entrySet()) {
//			prReasonerInput .createInternalElement(
//					Pair.ELEMENT_TYPE,
//					pair.getKey(),
//					null,null)
//					.setContents(pair.getValue());
//		}
//		for (Map.Entry<String,Predicate> pair : reasonerInput.predicates.entrySet()) {
//			((IPRPredicate)(prReasonerInput.createInternalElement(
//					IPRPredicate.ELEMENT_TYPE,
//					pair.getKey(),
//					null,null)))
//					.setPredicate(pair.getValue());
//		}
//		
//		for (Map.Entry<String,Expression> pair : reasonerInput.expressions.entrySet()) {
//			((IPRExpression)(prReasonerInput.createInternalElement(
//					IPRExpression.ELEMENT_TYPE,
//					pair.getKey(),
//					null,null)))
//					.setExpression(pair.getValue());
//		}
//		if (reasonerInput.hypAction != null){
//			((IPRHypAction)(prReasonerInput.createInternalElement(
//					IPRHypAction.ELEMENT_TYPE,
//					"hypAction",
//					null,null)))
//					.setAction(reasonerInput.hypAction);
//		}
//		
//		for (int i = 0; i < reasonerInput.children.length; i++) {
//			((PRReasonerInput)(prReasonerInput.createInternalElement(
//					IPRReasonerInput.ELEMENT_TYPE,
//					Integer.toString(i),
//					null,null)))
//					.setReasonerInput(reasonerInput.children[i]);
//		}
//		
//	}

	public void putPredicate(String name, Predicate predicate) throws SerializeException {
		try {
			((IPRPredicate)(createInternalElement(
					IPRPredicate.ELEMENT_TYPE,
					name,
					null,null)))
					.setPredicate(predicate);
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}

	public Predicate getPredicate(String name) throws SerializeException {
		InternalElement prPredicate = getInternalElement(IPRPredicate.ELEMENT_TYPE,name);
		if (! prPredicate.exists()) return null;
		try {
			return ((IPRPredicate)prPredicate).getPredicate();
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}
	
	public void putString(String name, String string) throws SerializeException {
		try {
			((IPair)(createInternalElement(
					IPair.ELEMENT_TYPE,
					name,
					null,null)))
					.setContents(string);
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}
	
	public String getString(String name) throws SerializeException {
		InternalElement pair = getInternalElement(IPair.ELEMENT_TYPE,name);
		if (! pair.exists()) return null;
		try {
			return ((IPair)pair).getContents();
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}

	public void putExpression(String name, Expression expression) throws SerializeException {
		try {
			((IPRExpression)(createInternalElement(
					IPRExpression.ELEMENT_TYPE,
					name,
					null,null)))
					.setExpression(expression);
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}

	public Expression getExpression(String name) throws SerializeException {
		InternalElement prExpression = getInternalElement(IPRExpression.ELEMENT_TYPE,name);
		if (! prExpression.exists()) return null;
		try {
			return ((IPRExpression)prExpression).getExpression();
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}

	public IReasonerInputSerializer[] getSubInputSerializers() throws SerializeException {
		try {
			// TODO : do cast efficiently.
			IRodinElement[] rodinElements = getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
			IReasonerInputSerializer[] subInputSerializers = new IReasonerInputSerializer[rodinElements.length];
			for (int i = 0; i < subInputSerializers.length; i++) {
				subInputSerializers[i] =  (IPRReasonerInput)rodinElements[i];
			}
			return subInputSerializers;
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
	}

	public IReasonerInputSerializer[] makeSubInputSerializers(int length) throws SerializeException{
		assert length > 0;
		IReasonerInputSerializer[] subInputSerializers = 
			new IReasonerInputSerializer[length];
		try {
			assert getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE).length == 0;
			for (int i = 0; i < length; i++) {
				subInputSerializers[i] = 
					(IPRReasonerInput) createInternalElement(
							IPRReasonerInput.ELEMENT_TYPE,
							Integer.toString(i),
							null,
							null);
			}
		} catch (RodinDBException e) {
			throw new SerializeException(e);
		}
		return subInputSerializers;
	}
}
