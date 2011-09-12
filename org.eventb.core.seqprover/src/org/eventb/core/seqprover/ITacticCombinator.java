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
package org.eventb.core.seqprover;

import java.util.List;

/**
 * Common protocol for tactic combinators.
 * <p>
 * This interface is intended to be implemented by clients who contribute tactic
 * combinators (extension point 'tacticCombinators').
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * 
 */
public interface ITacticCombinator {

	/**
	 * Returns a combined tactic out if the given tactics.
	 * <p>
	 * The size of the given list is guaranteed to respect the arity specified
	 * in the extension.
	 * </p>
	 * 
	 * @param tactics
	 *            a list of tactics
	 * @return a combined tactic
	 */
	ITactic getTactic(List<ITactic> tactics);

}
