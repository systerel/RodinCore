/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IAutoTacticCheckResult;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Internal implementation of the published interface.
 * 
 * @author Laurent Voisin
 */
public class AutoTacticCheckResult implements IAutoTacticCheckResult {

	private final ITacticDescriptor descriptor;
	private final Object result;
	private final boolean fresh;

	// Creates a cached result
	public AutoTacticCheckResult(ITacticDescriptor descriptor) {
		this.descriptor = descriptor;
		this.result = null;
		this.fresh = false;
	}

	// Creates a fresh result
	public AutoTacticCheckResult(ITacticDescriptor descriptor, Object result) {
		this.descriptor = descriptor;
		this.result = result;
		this.fresh = true;
	}

	@Override
	public ITacticDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public boolean isFresh() {
		return fresh;
	}

	// For debugging purposes
	@Override
	public String toString() {
		if (!fresh) {
			return descriptor.getTacticID() + ": OK (cached)";
		}
		if (result == null) {
			return descriptor.getTacticID() + ": OK";
		}
		return descriptor.getTacticID() + ": " + result;
	}
}
