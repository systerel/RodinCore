/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.IEvent;
import org.eventb.core.sc.state.ICurrentEvent;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.tool.state.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class CurrentEvent extends State implements ICurrentEvent {

	private final IEvent event;
	private final IEventSymbolInfo eventSymbolInfo;
	private final boolean isInitialisation;
	
	public CurrentEvent(IEvent event, IEventSymbolInfo eventSymbolInfo) {
		this.event = event;
		this.eventSymbolInfo = eventSymbolInfo;
		isInitialisation = eventSymbolInfo.getSymbol().equals(IEvent.INITIALISATION);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ICurrentEvent#getCurrentEvent()
	 */
	public IEvent getCurrentEvent() {
		return event;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public IEventSymbolInfo getCurrentEventSymbolInfo() {
		return eventSymbolInfo;
	}

	public boolean isInitialisation() {
		return isInitialisation;
	}

}
