/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added IReasonerDesc (field and constructor)
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;

public abstract class ReasonerOutput implements IReasonerOutput {

	private static IReasonerDesc getDesc(IReasoner reasoner) {
		final String reasonerId = reasoner.getReasonerID();
		final ReasonerRegistry registry = ReasonerRegistry
				.getReasonerRegistry();
		if (!registry.isRegistered(reasonerId)) {
			// in case the reasoner is not registered, we just store a
			// descriptor that contains the reasoner as instance;
			// it is not registered in order to to avoid side effects
			return ReasonerDesc.makeUnknownReasonerDesc(reasoner);
		}
		// live version
		return registry.getLiveReasonerDesc(reasonerId);
	}

	protected final IReasonerDesc reasonerDesc;
	protected final IReasonerInput generatedUsing;

	public ReasonerOutput(IReasoner generatedBy, IReasonerInput generatedUsing) {
		this.reasonerDesc = getDesc(generatedBy);
		this.generatedUsing = generatedUsing;
	}

	public ReasonerOutput(IReasonerDesc reasonerDesc,
			IReasonerInput generatedUsing) {
		this.reasonerDesc = reasonerDesc;
		this.generatedUsing = generatedUsing;
	}

	public IReasoner generatedBy() {
		return reasonerDesc.getInstance();
	}

	public IReasonerDesc getReasonerDesc() {
		return reasonerDesc;
	}

	public IReasonerInput generatedUsing() {
		return generatedUsing;
	}
	

}
