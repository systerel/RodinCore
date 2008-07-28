/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventRefinesInfo extends State implements IEventRefinesInfo {

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
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public List<IAbstractEventInfo> getAbstractEventInfos() throws CoreException {
		assertImmutable();
		return abstractInfos;
	}

	public EventRefinesInfo(int size) {
		abstractInfos = new ArrayList<IAbstractEventInfo>(size);
		refEvents = new ArrayList<IRefinesEvent>(size);
	}
	
	public boolean currentEventIsNew() throws CoreException {
		assertImmutable();
		return abstractInfos.size() == 0 && refEvents.size() == 0;
	}

	public void addAbstractEventInfo(IAbstractEventInfo info) throws CoreException {
		assertMutable();
		abstractInfos.add(info);
	}

	public List<IRefinesEvent> getRefinesClauses() throws CoreException {
		assertImmutable();
		return refEvents;
	}

	public void addRefinesEvent(IRefinesEvent refinesEvent) throws CoreException {
		assertMutable();
		refEvents.add(refinesEvent);
	}

}
