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

import org.eventb.core.sc.IAbstractEventInfo;
import org.eventb.core.sc.IRefinedEventTable;

/**
 * @author Stefan Hallerstede
 *
 */
public class RefinedEventTable implements IRefinedEventTable {

	private final List<IAbstractEventInfo> array;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	public List<IAbstractEventInfo> getAbstractEventInfos() {
		return array;
	}

	public RefinedEventTable(int size) {
		array = new ArrayList<IAbstractEventInfo>(size);
	}
	
	public int size() {
		return array.size();
	}

	public void addAbstractEventInfo(IAbstractEventInfo info) {
		array.add(info);
	}

}
