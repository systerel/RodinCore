/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.tool.state.IToolStateType;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventInfo extends ToolState implements IAbstractEventInfo {

	@Override
	public void makeImmutable() {
		super.makeImmutable();
		splitInfos = Collections.unmodifiableList(splitInfos);
		mergeInfos = Collections.unmodifiableList(mergeInfos);
		idents = Collections.unmodifiableList(idents);
		guards = Collections.unmodifiableList(guards);
		actions = Collections.unmodifiableList(actions);
	}

	private final String label;
	private IEventSymbolInfo implicitRefinedInfo;
	private boolean refineError;
	private List<IEventSymbolInfo> splitInfos;
	private List<IEventSymbolInfo> mergeInfos;
	private Hashtable<String,FreeIdentifier> table;
	private final ISCEvent event;
	private List<FreeIdentifier> idents;
	private List<Predicate> guards;
	private List<Assignment> actions;
	
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
			table = new Hashtable<String,FreeIdentifier>(idents.size() * 4 / 3 + 1);
			for (FreeIdentifier identifier : idents) {
				table.put(identifier.getName(), identifier);
			}
		}
		return table.get(name);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifiers()
	 */
	public List<FreeIdentifier> getVariables() {
		return idents;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getGuards()
	 */
	public List<Predicate> getGuards() {
		return guards;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getActions()
	 */
	public List<Assignment> getActions() {
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
		this.idents = Arrays.asList(idents);
		this.guards = Arrays.asList(guards);
		this.actions = Arrays.asList(actions);
		refineError = false;
		implicitRefinedInfo = null;
		splitInfos = new LinkedList<IEventSymbolInfo>();
		mergeInfos = new LinkedList<IEventSymbolInfo>();
	}

	public boolean isRefined() {
		return implicitRefinedInfo != null || splitInfos.size() != 0 || mergeInfos.size() != 0;
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
	
	@Override
	public boolean equals(Object obj) {
		IAbstractEventInfo info = (IAbstractEventInfo) obj;
		return label.equals(info.getEventLabel());
	}

	public ISCEvent getEvent() {
		return event;
	}

	public void setRefineError(boolean value) throws CoreException {
		assertMutable();
		refineError = value;
	}

	public boolean hasRefineError() {
		return refineError;
	}

	public void addMergeSymbolInfo(IEventSymbolInfo symbolInfo) throws CoreException {
		assertMutable();
		mergeInfos.add(symbolInfo);
	}

	public void addSplitSymbolInfo(IEventSymbolInfo symbolInfo) throws CoreException {
		assertMutable();
		splitInfos.add(symbolInfo);
	}

	public List<IEventSymbolInfo> getMergeSymbolInfos() {
		return mergeInfos;
	}

	public List<IEventSymbolInfo> getSplitSymbolInfos() {
		return splitInfos;
	}

	public void setImplicit(IEventSymbolInfo eventSymbolInfo) throws CoreException {
		assertMutable();
		implicitRefinedInfo = eventSymbolInfo;
	}

	public IEventSymbolInfo getImplicit() {
		return implicitRefinedInfo;
	}

	public IToolStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
