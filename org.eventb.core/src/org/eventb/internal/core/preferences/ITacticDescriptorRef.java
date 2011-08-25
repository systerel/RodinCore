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
package org.eventb.internal.core.preferences;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * Common protocol for tactic descriptor references.
 * <p>
 * Instances of this interface are intended to occur in preference maps, as
 * references to preference units.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * TODO move to public API if required
 */
public interface ITacticDescriptorRef extends ITacticDescriptor {
	/**
	 * Returns whether this reference is valid at method call time.
	 * 
	 * @return <code>true</code> iff this reference is valid
	 */
	boolean isValidReference();
}
