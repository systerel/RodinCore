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

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.OK_STATUS;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.seqprover.IAutoTacticCheckResult;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * Internal implementation of the published interface.
 * 
 * @author Laurent Voisin
 */
public class AutoTacticCheckResult implements IAutoTacticCheckResult {

	private final ITacticDescriptor descriptor;
	private final boolean fresh;
	private final IStatus status;

	// Creates a cached result
	public AutoTacticCheckResult(ITacticDescriptor descriptor) {
		this.descriptor = descriptor;
		this.fresh = false;
		this.status = OK_STATUS;
	}

	// Creates a fresh result
	public AutoTacticCheckResult(ITacticDescriptor descriptor, Object result) {
		this.descriptor = descriptor;
		this.fresh = true;
		this.status = statusFromResult(result);
	}

	private static IStatus statusFromResult(Object result) {
		if (result == null) {
			return OK_STATUS;
		}

		final Throwable reason;
		final String message;
		if (result instanceof Throwable) {
			reason = (Throwable) result;
			message = reason.getLocalizedMessage();
		} else {
			reason = null;
			message = result.toString();
		}
		return new Status(ERROR, SequentProver.PLUGIN_ID, message, reason);
	}

	@Override
	public ITacticDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public IStatus getStatus() {
		return status;
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
		if (status.isOK()) {
			return descriptor.getTacticID() + ": OK";
			
		}
		return descriptor.getTacticID() + ": ERROR: " + status.getMessage();
	}
}
