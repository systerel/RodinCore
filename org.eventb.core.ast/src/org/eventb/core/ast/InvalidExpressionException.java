/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

/**
 * Exception raised when a service has been called on an unsupported expression.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class InvalidExpressionException extends Exception {

	private static final long serialVersionUID = 623129663874885073L;

	/**
	 * Creates a new instance of this exception.
	 */
	public InvalidExpressionException() {
		super();
	}

}
