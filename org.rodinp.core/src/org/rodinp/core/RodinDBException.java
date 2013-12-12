/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.JavaModelException
 *******************************************************************************/
package org.rodinp.core;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.rodinp.internal.core.RodinDBStatus;

/**
 * A checked exception representing a failure in the Rodin database. Rodin
 * database exceptions contain a Rodin-specific status object describing the
 * cause of the exception.
 *
 * @see IRodinDBStatus
 * @see IRodinDBStatusConstants
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class RodinDBException extends CoreException {

	private static final long serialVersionUID = 6901416113578383774L;

	CoreException nestedCoreException;

	/**
	 * Creates a Rodin database exception that wraps around the given
	 * <code>Throwable</code>. The exception contains a Rodin-specific status
	 * object with severity <code>IStatus.ERROR</code> and the given status
	 * code.
	 * 
	 * @param e
	 *            the <code>Throwable</code>
	 * @param code
	 *            one of the Rodin-specific status codes declared in
	 *            <code>IRodinDBStatusConstants</code>
	 * @see IRodinDBStatusConstants
	 * @see org.eclipse.core.runtime.IStatus#ERROR
	 */
	public RodinDBException(Throwable e, int code) {
		this(new RodinDBStatus(code, e));
	}

	/**
	 * Creates a Rodin database exception for the given
	 * <code>CoreException</code>. Equivalent to
	 * <code>RodinDBException(exception,IRodinDBStatusConstants.CORE_EXCEPTION</code>.
	 * 
	 * @param exception
	 *            the <code>CoreException</code>
	 */
	public RodinDBException(CoreException exception) {
		super(exception.getStatus());
		this.nestedCoreException = exception;
	}

	/**
	 * Creates a Rodin database exception for the given Rodin-specific status
	 * object.
	 * 
	 * @param status
	 *            the Rodin-specific status object
	 */
	public RodinDBException(IRodinDBStatus status) {
		super(status);
	}

	/**
	 * Returns the underlying <code>Throwable</code> that caused the failure.
	 * 
	 * @return the wrapped up <code>Throwable</code>, or <code>null</code>
	 *         if the direct cause of the failure was at the Rodin database layer
	 */
	public Throwable getException() {
		if (this.nestedCoreException == null) {
			return getStatus().getException();
		} else {
			return this.nestedCoreException;
		}
	}

	/**
	 * Returns the Rodin database status object for this exception. Equivalent
	 * to <code>(IRodinDBStatus) getStatus()</code>.
	 * 
	 * @return a status object
	 */
	public IRodinDBStatus getRodinDBStatus() {
		IStatus status = this.getStatus();
		if (status instanceof IRodinDBStatus) {
			return (IRodinDBStatus) status;
		} else {
			// A regular IStatus is created only in the case of a CoreException.
			// See bug 13492 Should handle RodinDBExceptions that contains
			// CoreException more gracefully
			return new RodinDBStatus(this.nestedCoreException);
		}
	}

	/**
	 * Returns whether this exception indicates that a Rodin database element
	 * does not exist. Such exceptions have a status with a code of
	 * <code>IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST</code>. This
	 * is a convenience method.
	 * 
	 * @return <code>true</code> if this exception indicates that a Rodin
	 *         database element does not exist
	 * @see IRodinDBStatus#isDoesNotExist()
	 * @see IRodinDBStatusConstants#ELEMENT_DOES_NOT_EXIST
	 */
	public boolean isDoesNotExist() {
		IRodinDBStatus rodinDBStatus = getRodinDBStatus();
		return rodinDBStatus != null && rodinDBStatus.isDoesNotExist();
	}

	/**
	 * Prints this exception's stack trace to the given print stream.
	 * 
	 * @param output
	 *            the print stream
	 * @since 3.0
	 */
	@Override
	public void printStackTrace(PrintStream output) {
		synchronized (output) {
			super.printStackTrace(output);
			Throwable throwable = getException();
			if (throwable != null) {
				output.print("Caused by: "); //$NON-NLS-1$
				throwable.printStackTrace(output);
			}
		}
	}

	/**
	 * Prints this exception's stack trace to the given print writer.
	 * 
	 * @param output
	 *            the print writer
	 * @since 3.0
	 */
	@Override
	public void printStackTrace(PrintWriter output) {
		synchronized (output) {
			super.printStackTrace(output);
			Throwable throwable = getException();
			if (throwable != null) {
				output.print("Caused by: "); //$NON-NLS-1$
				throwable.printStackTrace(output);
			}
		}
	}

	/*
	 * Returns a printable representation of this exception suitable for
	 * debugging purposes only.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Rodin Database Exception: "); //$NON-NLS-1$
		if (getException() != null) {
			if (getException() instanceof CoreException) {
				CoreException c = (CoreException) getException();
				buffer.append("Core Exception [code "); //$NON-NLS-1$
				buffer.append(c.getStatus().getCode());
				buffer.append("] "); //$NON-NLS-1$
				buffer.append(c.getStatus().getMessage());
			} else {
				buffer.append(getException().toString());
			}
		} else {
			buffer.append(getStatus().toString());
		}
		return buffer.toString();
	}
}
