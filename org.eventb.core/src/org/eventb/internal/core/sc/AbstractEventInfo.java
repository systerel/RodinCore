/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventInfo implements IAbstractEventInfo {

	private final String label;
	private boolean forbidden;
	private IEventSymbolInfo inheritedEvent;
	private boolean refineError;
	private final LinkedList<IEventSymbolInfo> splitInfos;
	private final LinkedList<IEventSymbolInfo> mergeInfos;
	private Hashtable<String,FreeIdentifier> table;
	private final ISCEvent event;
	private final FreeIdentifier[] idents;
	private final Predicate[] guards;
	private final Assignment[] actions;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getEventLabel()
	 */
	public String getEventLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifier(java.lang.String)
	 */
	public FreeIdentifier getIdentifier(String name) {
		if (table == null) {
			table = new Hashtable<String,FreeIdentifier>(idents.length * 4 / 3 + 1);
			for (FreeIdentifier identifier : idents) {
				table.put(identifier.getName(), identifier);
			}
		}
		return table.get(name);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifiers()
	 */
	public FreeIdentifier[] getIdentifiers() {
		return idents;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getGuards()
	 */
	public Predicate[] getGuards() {
		return guards;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getActions()
	 */
	public Assignment[] getActions() {
		return actions;
	}

	public AbstractEventInfo(
			ISCEvent event,
			String label, 
			FreeIdentifier[] idents, 
			Predicate[] guards, 
			Assignment[] actions) {
		this.event = event;
		this.label = label;
		this.idents = idents;
		this.guards = guards;
		this.actions = actions;
		refineError = false;
		inheritedEvent = null;
		forbidden = false;
		splitInfos = new LinkedList<IEventSymbolInfo>();
		mergeInfos = new LinkedList<IEventSymbolInfo>();
	}

	public boolean isRefined() {
		return inheritedEvent != null || splitInfos.size() != 0 || mergeInfos.size() != 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return label.hashCode();
	}

	public int compareTo(Object o) {
		IAbstractEventInfo info = (IAbstractEventInfo) o;
		return label.compareTo(info.getEventLabel());
	}

	public void setForbidden(boolean value) {
		forbidden = value;
	}

	public boolean isForbidden() {
		return forbidden;
	}

	public ISCEvent getEvent() {
		return event;
	}

	public void setRefineError(boolean value) {
		refineError = value;
	}

	public boolean hasRefineError() {
		return refineError;
	}

	public void addMergeSymbolInfo(IEventSymbolInfo symbolInfo) {
		mergeInfos.add(symbolInfo);
	}

	public void addSplitSymbolInfo(IEventSymbolInfo symbolInfo) {
		splitInfos.add(symbolInfo);
	}

	public List<IEventSymbolInfo> getMergeSymbolInfos() {
		return mergeInfos;
	}

	public List<IEventSymbolInfo> getSplitSymbolInfos() {
		return splitInfos;
	}

	public void setInherited(IEventSymbolInfo eventSymbolInfo) {
		inheritedEvent = eventSymbolInfo;
	}

	public IEventSymbolInfo getInherited() {
		return inheritedEvent;
	}

}
