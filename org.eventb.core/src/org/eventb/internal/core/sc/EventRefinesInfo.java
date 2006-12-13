/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.internal.core.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventRefinesInfo extends State implements IEventRefinesInfo {

	private final List<IAbstractEventInfo> array;
	private final List<IRefinesEvent> refEvents;
	private final IEventSymbolInfo symbolInfo;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public List<IAbstractEventInfo> getAbstractEventInfos() {
		return array;
	}

	public EventRefinesInfo(IEventSymbolInfo symbolInfo, int size) {
		array = new ArrayList<IAbstractEventInfo>(size);
		refEvents = new ArrayList<IRefinesEvent>(size);
		this.symbolInfo = symbolInfo;
	}
	
	public boolean isEmpty() {
		return array.size() == 0 && refEvents.size() == 0;
	}

	public void addAbstractEventInfo(IAbstractEventInfo info) {
		array.add(info);
	}

	public IEvent getEvent() {
		return (IEvent) symbolInfo.getSourceElement();
	}

	public String getEventLabel() {
		return symbolInfo.getSymbol();
	}

	public List<IRefinesEvent> getRefinesEvents() {
		return refEvents;
	}

	public void addRefinesEvent(IRefinesEvent refinesEvent) {
		refEvents.add(refinesEvent);
	}

}
