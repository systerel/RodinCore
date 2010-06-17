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
package org.eventb.core.ast;

import java.util.Collection;

/**
 * Common protocol for accumulating findings while inspecting sub-formulas of a
 * formula.
 * 
 * @param <F>
 *            type of the findings to accumulate
 * 
 * @see IFormulaInspector
 * 
 * @author Laurent Voisin
 * @since 1.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IAccumulator<F> {

	/**
	 * Returns the position of the sub-formula currently considered in a call to
	 * <code>inspect</code>. This method must only be called within the frame of
	 * a call to an <code>inspect</code> method.
	 * 
	 * @return the position of the current sub-formula
	 * @throws IllegalStateException
	 *             if this method is called out of the frame of an
	 *             <code>inspect</code> method
	 */
	IPosition getCurrentPosition();

	/**
	 * Adds the given finding to the accumulator.
	 * 
	 * @param finding
	 *            a finding to accumulate
	 */
	void add(F finding);

	/**
	 * Adds the given findings to the accumulator.
	 * 
	 * @param findings
	 *            findings to accumulate
	 */
	void add(F... findings);

	/**
	 * Adds the given findings to the accumulator.
	 * 
	 * @param findings
	 *            findings to accumulate
	 */
	void add(Collection<F> findings);

}
