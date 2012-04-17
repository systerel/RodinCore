/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Collection;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * Common protocol for dynamically providing tactics to be used in tactic
 * profiles.
 * 
 * @author Nicolas Beauger
 * @since 2.5
 */
public interface IDynTacticProvider {

	/**
	 * Returns all available tactics that can be used in tactic profiles.
	 * 
	 * @return a collection of tactics
	 */
	Collection<ITacticDescriptor> getDynTactics();

}
