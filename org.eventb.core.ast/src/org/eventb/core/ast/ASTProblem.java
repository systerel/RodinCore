/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored hashcode() and equals()
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Arrays;

/**
 * Describes a problem encountered when dealing with a formula.
 * <p>
 * This class is not intended to be implemented by clients.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ASTProblem {
	
	private Object[] args;
	// necessary information to locate and indicate the problem
	private SourceLocation location;
	private ProblemKind msg;
	private int severity;
	
	/**
	 * @param location the location of this error or warning
	 * @param msg the problem of this problem
	 * @param severity the severity of the problem
	 * @param args the additional arguments needed to describe the problem. These arguments are best described here {@link ProblemKind}
	 */
	public ASTProblem(SourceLocation location, ProblemKind msg, int severity, Object... args) {
		this.location = location;
		this.msg = msg;
		this.args = args;
		this.severity = severity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + msg.hashCode();
		result = prime * result + severity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ASTProblem other = (ASTProblem) obj;
		if (this.msg != other.msg || severity != other.severity)
			return false;
		if (!Arrays.equals(args, other.args))
			return false;
		if (this.location == null) {
			if (other.location != null)
				return false;
		} else if (!this.location.equals(other.location))
			return false;
		return true;
	}

	/**
	 * Returns the arguments of this problem.
	 * 
	 * @return the arguments of this problem
	 */
	public Object[] getArgs() {
		return args;
	}
	
	/**
	 * Return the {@link ProblemKind} of this error of warning
	 * 
	 * @return the problem of this error of warning
	 */
	public ProblemKind getMessage() {
		return msg;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	/**
	 * Returns the source location of this error or warning.
	 * 
	 * @return the source location of the error or warning
	 */
	public SourceLocation getSourceLocation(){
		return location;
	}
	
	/**
	 * Returns <code>true</code> if this is an error.
	 * 
	 * @return <code>true</code> if this is an error
	 */
	public boolean isError() {
		return (this.severity & ProblemSeverities.Error) != 0;
	}
	
	/**
	 * Returns <code>true</code> if this is an warning.
	 * 
	 * @return <code>true</code> if this is an warning
	 */
	public boolean isWarning() {
		return (this.severity & ProblemSeverities.Error) == 0;
	}

	@Override
	public String toString() {
		final SourceLocation loc = getSourceLocation();
		final Object[] formatArgs;
		if (loc != null) {
			formatArgs = new Object[args.length + 2];
			System.arraycopy(args, 0, formatArgs, 0, args.length);
			formatArgs[args.length] = loc.getStart();
			formatArgs[args.length + 1] = loc.getEnd();
		} else {
			formatArgs = args;
		}
		return String.format(getMessage().toString(), formatArgs);
	}
	
}
