/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier;

import org.eventb.core.seqprover.IProofMonitor;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class Simplifier<T> {

	public abstract T simplify(T t, IProofMonitor monitor) throws CancelException;

	protected void checkCancel(IProofMonitor monitor) throws CancelException {
		if (monitor.isCanceled())
			throw new CancelException();
	}

	public static class CancelException extends Exception {
		private static final long serialVersionUID = 5520010779657323319L;
	}
}
