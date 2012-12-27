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
package org.eventb.internal.pp.core.provers.seedsearch.solver;

import java.util.HashSet;
import java.util.Set;

final class InstantiableContainer {

	private final Set<VariableLink> transmitorLinks;
	private boolean transmitted;
	
	InstantiableContainer() {
		this.transmitorLinks = new HashSet<VariableLink>();
		this.transmitted = false;
	}
	
	InstantiableContainer(VariableLink link) {
		this.transmitorLinks = new HashSet<VariableLink>();
		transmitorLinks.add(link);
		this.transmitted = true;
	}
	
	boolean hasTransmitorLink(VariableLink link) {
		return transmitorLinks.contains(link);
	}
	
	boolean isValid() {
		return !transmitted || !transmitorLinks.isEmpty();
	}
	
	void removeTransmitorLink(VariableLink link) {
		assert transmitorLinks.contains(link);
		
		transmitorLinks.remove(link);
	}
	
	
	boolean isTransmitted() {
		return transmitted;
	}
	
	void setNotTransmitted() {
		transmitted = false;
	}
	
	void addTransmitorLink(VariableLink link) {
		transmitorLinks.add(link);
	}
	
}
