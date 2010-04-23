/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

/**
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CycleError extends Exception {

	private static final long serialVersionUID = 3961303994056706546L;

	public CycleError(String reason) {
		super(reason);
	}
}