/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IIdentifierTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class IdentifierTable implements IIdentifierTable {

	final private TreeSet<FreeIdentifier> identifiers;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<FreeIdentifier> iterator() {
		return identifiers.iterator();
	}
	
	private TreeSet<FreeIdentifier> makeTreeSet() {
		return new TreeSet<FreeIdentifier>(new Comparator<FreeIdentifier>() {

			public int compare(FreeIdentifier o1, FreeIdentifier o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
	}

	public IdentifierTable() {
		identifiers = makeTreeSet();
	}
	
	public IdentifierTable(IIdentifierTable table) {
		identifiers = makeTreeSet();
		for (FreeIdentifier identifier : table)
			identifiers.add(identifier);
	}
	
	public void addIdentifier(FreeIdentifier identifier) {
		identifiers.add(identifier);
	}

	public boolean containsIdentifier(FreeIdentifier identifier) {
		return identifiers.contains(identifier);
	}

	public void save(IInternalParent parent, IProgressMonitor monitor) throws RodinDBException {
		for (FreeIdentifier identifier : identifiers) {
			final String identName = identifier.getName();
			final Type identType = identifier.getType();
			final IPOIdentifier ident = 
				(IPOIdentifier) parent.createInternalElement(
						IPOIdentifier.ELEMENT_TYPE, identName, null, monitor);
			ident.setType(identType);		
		}

	}

}
