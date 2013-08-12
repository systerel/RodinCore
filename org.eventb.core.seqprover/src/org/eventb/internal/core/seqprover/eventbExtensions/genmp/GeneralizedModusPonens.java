/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;


/**
 * Simplifies the visible hypotheses and goal in a sequent by replacing
 * sub-predicates <code>P</code> by <code>⊤</code> (or <code>⊥</code>) if
 * <code>P</code> (or <code>¬P</code>) appears as hypothesis (global and local).
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonens extends AbstractGenMP {

	public GeneralizedModusPonens() {
		super(Level.L0);
	}
	
}