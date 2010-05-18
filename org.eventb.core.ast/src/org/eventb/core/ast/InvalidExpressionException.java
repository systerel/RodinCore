/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Exception raised when a service has been called on an unsupported expression.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
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
