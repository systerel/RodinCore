/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.state;

import org.eventb.core.state.IState;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class State implements IState {
	
	private boolean immutable;
	
	public State() {
		immutable = false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#isImmutable()
	 */
	public boolean isImmutable() {
		return immutable;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#makeImmutable()
	 */
	public void makeImmutable() {
		immutable = true;
	}

}
