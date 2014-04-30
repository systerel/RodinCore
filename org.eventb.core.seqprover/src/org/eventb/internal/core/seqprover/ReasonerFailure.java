/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;


public class ReasonerFailure extends ReasonerOutput implements IReasonerFailure{
	
	private String reason;
	
	public ReasonerFailure(IReasoner generatedBy, IReasonerInput generatedUsing, String error){
		super(generatedBy,generatedUsing);
		this.reason = error;
	}
	
	@Override
	public String getReason(){
		return this.reason;
	}
	
	@Override
	public String toString(){
		return this.reason;
	}

}
