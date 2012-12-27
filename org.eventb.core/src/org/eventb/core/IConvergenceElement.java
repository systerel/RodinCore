/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B convergence properties of an event.
 * <p>
 * A convergence is represented by an integer constant. Only the specified values
 * {@link Convergence#ORDINARY}, {@link Convergence#CONVERGENT}, and
 * {@link Convergence#ANTICIPATED} are permitted.
 * </p>
 * <p>
 * The attribute storing the convergence is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IConvergenceElement extends IInternalElement {

	/**
	 * The enumerated type <code>Convergence</code> specifies the different convergence
	 * types of events. Each convergence type is associated with an integer code used to
	 * represent the corresponding type in the database. The codes are public and can be
	 * used when it is necessary to access the database without this interface.
	 * <p>
	 * The order of the convergence values in the declaration is relevant. 
	 * <code>ORDINARY</code> is the weakest and <code>CONVERGENT</code> the strongest
	 * requirement. This is relevant in merge refinements where merging of the 
	 * abstract events gives a combined convergence. Let <code>absConv</code> be a
	 * {@link Collection} of convergences of the abstract events (containing all convergence
	 * values appearing in one of the abstract events), then the combined convergence can be
	 * calculated by
	 * <pre>
	 * combConv = Collections.min(absConv);
	 * </pre>
	 * </p>
	 * This calculation is used in the Event-B core static checker and proof obligation generator.
	 */
	enum Convergence {
		ORDINARY(0),
		ANTICIPATED(2),
		CONVERGENT(1);
		
		private final int code;

		Convergence(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
		private static final IConvergenceElement.Convergence[] convergences = 
			new IConvergenceElement.Convergence[] {
				IConvergenceElement.Convergence.ORDINARY,
				IConvergenceElement.Convergence.CONVERGENT,
				IConvergenceElement.Convergence.ANTICIPATED
			};
		
		public static Convergence valueOf(int n) {
			if (n<0 || n>2)
				throw new IllegalArgumentException("Convergence value out of range");
			return convergences[n];
		}
		
	}
	
	/**
	 * Tests whether the convergence is defined or not.
	 * 
	 * @return whether the convergence is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasConvergence() throws RodinDBException;
	
	/**
	 * Sets the convergence to one of the values {@link Convergence#ORDINARY}, 
	 * {@link Convergence#CONVERGENT}, or {@link Convergence#ANTICIPATED}.
	 * @param value the convergence to be set
	 * @param monitor 
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setConvergence(Convergence value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the convergence stored in this attribute.
	 * 
	 * @return the convergence; one of  {@link Convergence#ORDINARY}, 
	 * {@link Convergence#CONVERGENT}, {@link Convergence#ANTICIPATED}
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	Convergence getConvergence() throws RodinDBException;
}
