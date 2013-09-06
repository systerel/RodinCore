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
package org.eventb.internal.core.sc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.tool.IStateType;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ConcreteEventInfo extends AccuracyInfo implements IConcreteEventInfo {

	@Override
	public String toString() {
		return abstractInfos.toString();
	}

	@Override
	public void makeImmutable() {
		super.makeImmutable();
		abstractInfos = Collections.unmodifiableList(abstractInfos);
		refEvents = Collections.unmodifiableList(refEvents);
	}

	private List<IAbstractEventInfo> abstractInfos;
	private List<IRefinesEvent> refEvents;
	private final ILabelSymbolInfo symbolInfo;
	private final IEvent event;
	private final String eventLabel;
	private final boolean isInit;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public List<IAbstractEventInfo> getAbstractEventInfos()
			throws CoreException {
		return abstractInfos;
	}

	public ConcreteEventInfo(IEvent event, ILabelSymbolInfo symbolInfo) {
		this.event = event;
		this.eventLabel = symbolInfo.getSymbol();
		this.symbolInfo = symbolInfo;
		isInit = eventLabel.equals(IEvent.INITIALISATION);
		abstractInfos = new LinkedList<IAbstractEventInfo>();
		refEvents = new LinkedList<IRefinesEvent>();
	}

	@Override
	public boolean eventIsNew() throws CoreException {
		return abstractInfos.size() == 0 && refEvents.size() == 0;
	}

	@Override
	public List<IRefinesEvent> getRefinesClauses() throws CoreException {
		return refEvents;
	}

	@Override
	public ILabelSymbolInfo getSymbolInfo() {
		return symbolInfo;
	}

	@Override
	public IEvent getEvent() {
		return event;
	}

	@Override
	public String getEventLabel() {
		return eventLabel;
	}

	@Override
	public boolean isInitialisation() {
		return isInit;
	}

}
