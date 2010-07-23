/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventInfo extends ConvergenceInfo implements IAbstractEventInfo {

	@Override
	public String toString() {
		return getEventLabel();
	}

	@Override
	public void makeImmutable() {
		super.makeImmutable();
		mergers = Collections.unmodifiableList(mergers);
		splitters = Collections.unmodifiableList(splitters);
	}

	private final String label;
	private Hashtable<String,FreeIdentifier> table;
	private final ISCEvent event;
	private List<FreeIdentifier> idents;
	private List<Predicate> guards;
	private List<Assignment> actions;
	
	private List<IConcreteEventInfo> mergers;
	private List<IConcreteEventInfo> splitters;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getEventLabel()
	 */
	@Override
	public String getEventLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifier(java.lang.String)
	 */
	@Override
	public FreeIdentifier getParameter(String name) throws CoreException {
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
	@Override
	public List<FreeIdentifier> getParameters() throws CoreException {
		return idents;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getGuards()
	 */
	@Override
	public List<Predicate> getGuards() throws CoreException {
		return guards;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getActions()
	 */
	@Override
	public List<Assignment> getActions() throws CoreException {
		return actions;
	}

	public AbstractEventInfo(
			ISCEvent event,
			String label, 
			IConvergenceElement.Convergence convergence,
			FreeIdentifier[] idents, 
			Predicate[] guards, 
			Assignment[] actions) throws RodinDBException {
		super(convergence);
		this.event = event;
		this.label = label;
		this.idents = Collections.unmodifiableList(Arrays.asList(idents));
		this.guards = Collections.unmodifiableList(Arrays.asList(guards));
		this.actions = Collections.unmodifiableList(Arrays.asList(actions));
		this.mergers = new ArrayList<IConcreteEventInfo>(2);
		this.splitters = new ArrayList<IConcreteEventInfo>(3);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return label.hashCode();
	}

	@Override
	public int compareTo(IAbstractEventInfo info) {
		return label.compareTo(info.getEventLabel());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return obj instanceof IAbstractEventInfo 
			&& label.equals(((IAbstractEventInfo) obj).getEventLabel());
	}

	@Override
	public ISCEvent getEvent() {
		return event;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public List<IConcreteEventInfo> getMergers() {
		return mergers;
	}

	@Override
	public List<IConcreteEventInfo> getSplitters() {
		return splitters;
	}

}
