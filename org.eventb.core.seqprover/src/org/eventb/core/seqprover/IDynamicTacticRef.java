/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for dynamic tactic references.
 * <p>
 * A dynamic tactic reference bears the id, name and description of a dynamic
 * tactic. It differs from the dynamic tactic itself in that the tactic
 * instantiation calls the registry again in order to fetch a fresh version of
 * the dynamic tactic, if available (or else a placeholder).
 * </p>
 * <p>
 * Instances of this interface are intended to be obtained by calling
 * {@link IAutoTacticRegistry#getDynTacticRef(String)} or
 * {@link IAutoTacticRegistry#getDynTacticRefs()}.
 * </p>
 * 
 * @author beauger
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.0
 */
public interface IDynamicTacticRef extends ITacticDescriptor {
	// no more methods
}
