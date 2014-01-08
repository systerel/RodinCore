/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Nicolas Beauger
 * @since 2.2
 * 
 */
public class PRReasoner extends InternalElement implements IPRReasoner {

	public PRReasoner(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IReasonerDesc getReasoner() throws RodinDBException {
		final String rid = getAttributeValue(EventBAttributes.PR_REASONER_ID_ATTRIBUTE);
		final IReasonerRegistry registry = SequentProver.getReasonerRegistry();
		return registry.getReasonerDesc(rid);
	}

	@Override
	public void setReasoner(IReasonerDesc reasoner, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.PR_REASONER_ID_ATTRIBUTE,
				reasoner.getVersionedId(), monitor);
	}

}
