/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added refined event informations
 *******************************************************************************/
package org.eventb.internal.core.sc;

import static org.eventb.core.ast.Formula.BFALSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IRefinesMachine;
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
	private HashMap<String,FreeIdentifier> table;
	private final ISCEvent event;
	private List<FreeIdentifier> idents;
	private List<Predicate> guards;
	private List<Assignment> actions;
	
	private List<IConcreteEventInfo> mergers;
	private List<IConcreteEventInfo> splitters;

	private final IRefinesMachine refinesMachine;

	private boolean refined = false;
	
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
			table = new HashMap<String,FreeIdentifier>(idents.size() * 4 / 3 + 1);
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
			Assignment[] actions,
			IRefinesMachine refinesMachine) throws RodinDBException {
		super(convergence);
		this.event = event;
		this.label = label;
		this.idents = Collections.unmodifiableList(Arrays.asList(idents));
		this.guards = Collections.unmodifiableList(Arrays.asList(guards));
		this.actions = Collections.unmodifiableList(Arrays.asList(actions));
		this.mergers = new ArrayList<IConcreteEventInfo>(2);
		this.splitters = new ArrayList<IConcreteEventInfo>(3);
		this.refinesMachine = refinesMachine;
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
	public IRefinesMachine getRefinesMachine() {
		return refinesMachine;
	}

	@Override
	public List<IConcreteEventInfo> getMergers() {
		return mergers;
	}

	@Override
	public List<IConcreteEventInfo> getSplitters() {
		return splitters;
	}

	@Override
	public void setRefined() {
		refined = true;
	}

	@Override
	public boolean getRefined() {
		return refined;
	}

	@Override
	public boolean isClosed() throws CoreException {
		for (Predicate guard : getGuards()) {
			if (guard.getTag() == BFALSE) {
				return true;
			}
		}
		return false;
	}
	
}
