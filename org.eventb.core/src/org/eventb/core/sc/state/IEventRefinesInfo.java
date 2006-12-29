/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventRefinesInfo extends IState {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".eventRefinesInfo";
	
	IEvent getEvent();
	String getEventLabel();
	
	List<IAbstractEventInfo> getAbstractEventInfos();
	
	boolean isEmpty();
	
	void addAbstractEventInfo(IAbstractEventInfo info);
	
	List<IRefinesEvent> getRefinesEvents();
	
	void addRefinesEvent(IRefinesEvent refinesEvent);
	
}
