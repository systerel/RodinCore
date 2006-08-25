/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.IAbstractEventInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventInfo implements IAbstractEventInfo {

	private final String label;
	private boolean forbidden;
	private boolean refined;
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
		refined = false;
	}

	public void setRefined(boolean value) {
		refined = value;
	}

	public boolean isRefined() {
		return refined;
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

}
