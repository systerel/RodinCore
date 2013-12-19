/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC events as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCEvent</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SCEvent extends EventBElement implements ISCEvent {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCEvent(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<ISCEvent> getElementType() {
		return ISCEvent.ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCRefinesClauses()
	 */
	@Override
	public ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException {
		return getChildrenOfType(ISCRefinesEvent.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getAbstractSCEvents()
	 */
	@Override
	public ISCEvent[] getAbstractSCEvents() throws CoreException {
		final ISCRefinesEvent[] refinesClauses =
			getChildrenOfType(ISCRefinesEvent.ELEMENT_TYPE);
		final ISCEvent[] result = new ISCEvent[refinesClauses.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = refinesClauses[i].getAbstractSCEvent();
		}
		return result;
	}
	
	@Override
	public ISCParameter[] getSCParameters() throws RodinDBException {
		return getChildrenOfType(ISCParameter.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCWitnesses()
	 */
	@Override
	public ISCWitness[] getSCWitnesses() throws RodinDBException {
		return getChildrenOfType(ISCWitness.ELEMENT_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCGuards()
	 */
	@Override
	public ISCGuard[] getSCGuards() throws RodinDBException {
		return getChildrenOfType(ISCGuard.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCActions()
	 */
	@Override
	public ISCAction[] getSCActions() throws RodinDBException {
		return getChildrenOfType(ISCAction.ELEMENT_TYPE); 
	}

	@Override
	public ISCAction getSCAction(String elementName) {
		return getInternalElement(ISCAction.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCGuard getSCGuard(String elementName) {
		return getInternalElement(ISCGuard.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCRefinesEvent getSCRefinesClause(String elementName) {
		return getInternalElement(ISCRefinesEvent.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCParameter getSCParameter(String elementName) {
		return getInternalElement(ISCParameter.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCWitness getSCWitness(String elementName) {
		return getInternalElement(ISCWitness.ELEMENT_TYPE, elementName);
	}


	/**
	 * @since 3.0
	 */
	@Override
	public ITypeEnvironmentBuilder getTypeEnvironment(ITypeEnvironment mchTypenv)
			throws CoreException {

		final FormulaFactory factory = mchTypenv.getFormulaFactory();
		ITypeEnvironmentBuilder typenv = mchTypenv.makeBuilder();
		for (ISCParameter par : getSCParameters()) {
			typenv.add(par.getIdentifier(factory));
		}
		return typenv;
	}

}
