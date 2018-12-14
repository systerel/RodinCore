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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.seqprover.ITacticCheckStatus;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * Internal implementation of the published interface.
 * 
 * @author Laurent Voisin
 */
public class TacticCheckStatus extends Status implements ITacticCheckStatus {

	// Creates a cached status
	public static TacticCheckStatus newStatus(ITacticDescriptor descriptor) {
		return new TacticCheckStatus(IStatus.OK, "OK (cached)", null, descriptor, false);
	}

	// Creates a fresh status
	public static TacticCheckStatus newStatus(ITacticDescriptor descriptor, Object result) {
		final int severity;
		final String message;
		final Throwable reason;
		if (result instanceof Throwable) {
			severity = IStatus.ERROR;
			reason = (Throwable) result;
			message = reason.getLocalizedMessage();
		} else {
			reason = null;
			severity = result == null ? IStatus.OK : IStatus.ERROR;
			message = result == null ? "OK" : result.toString();
		}
		return new TacticCheckStatus(severity, message, reason, descriptor, true);
	}

	private final ITacticDescriptor descriptor;
	private final boolean fresh;

	private TacticCheckStatus(int severity, String message, Throwable exception, //
			ITacticDescriptor descriptor, boolean fresh) {
		super(severity, SequentProver.PLUGIN_ID, message, exception);
		this.descriptor = descriptor;
		this.fresh = fresh;
	}

	@Override
	public String getMessage() {
		return descriptor.getTacticName() + ": " + super.getMessage();
	}

	@Override
	public ITacticDescriptor getDescriptor() {
		return descriptor;
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
		if (isOK()) {
			return descriptor.getTacticID() + ": OK";

		}
		return descriptor.getTacticID() + ": ERROR: " + getMessage();
	}

}
