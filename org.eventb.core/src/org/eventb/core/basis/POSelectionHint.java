/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class POSelectionHint extends EventBElement implements IPOSelectionHint {

	public POSelectionHint(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public IPOPredicateSet getEnd() throws RodinDBException {
		if (hasAttribute(EventBAttributes.POSELHINT_SND_ATTRIBUTE))
			return (IPOPredicateSet) getAttributeValue(EventBAttributes.POSELHINT_SND_ATTRIBUTE);
		else
			return null;
	}

	public IPOPredicate getPredicate() throws RodinDBException {
		IRodinElement element = getAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE);
		if (element instanceof IPOPredicate)
			return (IPOPredicate) element;
		else
			throw Util.newRodinDBException(Messages.database_POPredicateSelectionHintFailure);
	}

	public IPOPredicateSet getStart() throws RodinDBException {
		IRodinElement element = getAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE);
		if (element instanceof IPOPredicateSet)
			return (IPOPredicateSet) element;
		else
			throw Util.newRodinDBException(Messages.database_POIntervalSelectionHintFailure);
	}

	public void setInterval(IPOPredicateSet start, IPOPredicateSet end, IProgressMonitor monitor) 
	throws RodinDBException{
		setAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE, start, monitor);
		setAttributeValue(EventBAttributes.POSELHINT_SND_ATTRIBUTE, end, null);
	}

	public void setPredicate(IPOPredicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE, predicate, monitor);
		removeAttribute(EventBAttributes.POSELHINT_SND_ATTRIBUTE, null);
	}
	
}
