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
package org.eventb.internal.core.tool.types;

import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateRepository;
import org.eventb.core.tool.IStateType;

/**
 * Common protocol for states store in a state repository ({@link IStateRepository}).
 * To extend the state space of a core tool, i.e. static checker or proof 
 * obligation generator, a corresponding state, {@link ISCState} or {@link IPOGState} 
 * must be implemented.
 * <p>
 * A stub for an implementation of a state access protocol is offered.
 * <p>
 * A complex state built non-atomically should make use of it.
 * As long as the state is being computed it should be <i>mutable</i>.
 * Once the computation has finished the state should be made <i>immutable</i>.
 * An attempt to invoke a method that would change a state that is <i>immutable</i> 
 * should throw a <code>CoreException</code>. An attempt to invoke a method
 * that relies on the computation being terminated on a <i>mutable</i> state
 * should throw a <code>CoreException</code> too.
 * <p>
 * A state can only be set from mutable to immutable.
 * 
 * @see ISCState
 * @see IPOGState
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IState {

	/**
	 * Returns the type of this state.
	 * 
	 * @return the state type
	 */
	IStateType<?> getStateType();

	/**
	 * Makes the state immutable.
	 */
	void makeImmutable();
	
	/**
	 * Returns whether the state is immutable or not.
	 * 
	 * @return whether the state is immutable or not
	 */
	boolean isImmutable();
}
