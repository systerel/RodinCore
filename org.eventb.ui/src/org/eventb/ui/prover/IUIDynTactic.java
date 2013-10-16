/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Common protocol for dynamic tactics provided by {@link IUIDynTacticProvider}.
 * 
 * @author beauger
 * @since 3.0
 */
public interface IUIDynTactic {

	/**
	 * The name of this dynamic tactic.
	 * 
	 * @return a name
	 */
	String getName();

	/**
	 * The descriptor of this dynamic tactic.
	 * 
	 * @return a tactic descriptor
	 */
	ITacticDescriptor getTacticDescriptor();
}
