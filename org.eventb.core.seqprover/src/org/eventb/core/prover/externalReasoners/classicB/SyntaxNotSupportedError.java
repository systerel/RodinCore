/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.externalReasoners.classicB;

/**
 * @author halstefa
 *
 */
public class SyntaxNotSupportedError extends RuntimeException {
	
	private static final long serialVersionUID = 2956200880048375441L;

	public SyntaxNotSupportedError(String message) {
		super(message);
	}
}
