/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.internal.core.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventTable extends State implements IAbstractEventTable {
	
	final private ISCMachineFile machineFile;
	final private Hashtable<String, IAbstractEventInfo> table;
	final private HashSet<IAbstractEventInfo> set;
	final private HashSet<String> localVariables;

	public AbstractEventTable(int size, ISCMachineFile m) {
		table = new Hashtable<String, IAbstractEventInfo>(size);
		set = new HashSet<IAbstractEventInfo>(size);
		localVariables = new HashSet<String>(size);
		machineFile = m;
	}

	public String getStateType() {
		return STATE_TYPE;
	}

	public ISCMachineFile getMachineFile() {
		return machineFile;
	}

	public void putAbstractEventInfo(IAbstractEventInfo info) {
		if (set.add(info)) {
			table.put(info.getEventLabel(), info);
			
			for (FreeIdentifier identifier : info.getIdentifiers()) {
				localVariables.add(identifier.getName());
			}
		}
		else
			assert false;
		
	}

	public IAbstractEventInfo getAbstractEventInfo(String label) {
		return table.get(label);
	}

	public boolean isLocalVariable(String name) {
		return localVariables.contains(name);
	}

	public Iterator<IAbstractEventInfo> iterator() {
		return set.iterator();
	}

}
