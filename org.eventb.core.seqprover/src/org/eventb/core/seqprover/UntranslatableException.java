/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Exception thrown when a factory translation fails.
 * 
 * @author beauger
 * @since 3.0
 */
public class UntranslatableException extends Exception {

	private static final long serialVersionUID = -7255400480801949371L;

	public UntranslatableException(Throwable cause) {
		super(cause);
	}
}
