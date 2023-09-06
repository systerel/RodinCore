/*******************************************************************************
 * Copyright (c) 2023 Université de Lorraine.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.extension;

import org.eclipse.core.runtime.CoreException;

/**
 * Helper class encapsulating a {@link CoreException} in a
 * {@link RuntimeException}.
 *
 * This is used to throw a {@link CoreException} in implementations of
 * {@link IFormulaExtensionProvider} while keeping backward compatibility as
 * some methods do not have a {@code throws CoreException} declaration.
 *
 * @author Guillaume Verdier
 * @since 3.7
 */
public class RuntimeCoreException extends RuntimeException {

	private static final long serialVersionUID = -2674707425423481009L;

	/**
	 * Create a new runtime exception from a core exception.
	 *
	 * @param exception exception to encapsulate
	 */
	public RuntimeCoreException(CoreException exception) {
		super(exception);
	}

	/**
	 * Get the encapsulated exception.
	 *
	 * @return encapsulated exception
	 */
	@Override
	public CoreException getCause() {
		return (CoreException) super.getCause();
	}

}
