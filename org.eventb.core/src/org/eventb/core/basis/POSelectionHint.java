/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class POSelectionHint extends EventBPOElement implements IPOSelectionHint {

	public POSelectionHint(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IPOSelectionHint> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IPOPredicateSet getEnd() throws CoreException {
		if (hasAttribute(EventBAttributes.POSELHINT_SND_ATTRIBUTE))
			return (IPOPredicateSet) getTranslatedAttributeValue(EventBAttributes.POSELHINT_SND_ATTRIBUTE);
		else
			return null;
	}

	@Override
	public IPOPredicate getPredicate() throws CoreException {
		IRodinElement element = getTranslatedAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE);
		if (element instanceof IPOPredicate)
			return (IPOPredicate) element;
		else
			throw newCoreException(Messages.database_POPredicateSelectionHintFailure);
	}

	@Override
	public IPOPredicateSet getStart() throws CoreException {
		IRodinElement element = getTranslatedAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE);
		if (element instanceof IPOPredicateSet)
			return (IPOPredicateSet) element;
		else
			throw newCoreException(Messages.database_POIntervalSelectionHintFailure);
	}

	@Override
	public void setInterval(IPOPredicateSet start, IPOPredicateSet end,
			IProgressMonitor monitor) throws CoreException {
		setTranslatedAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE, start, monitor);
		setTranslatedAttributeValue(EventBAttributes.POSELHINT_SND_ATTRIBUTE, end, null);
	}

	@Override
	public void setPredicate(IPOPredicate predicate, IProgressMonitor monitor)
			throws CoreException {
		setTranslatedAttributeValue(EventBAttributes.POSELHINT_FST_ATTRIBUTE, predicate, monitor);
		removeAttribute(EventBAttributes.POSELHINT_SND_ATTRIBUTE, null);
	}
	
}
