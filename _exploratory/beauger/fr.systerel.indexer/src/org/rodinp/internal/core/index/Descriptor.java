/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IOccurrence;

public final class Descriptor {

	// Name of the described element. It is intended to be used as a public
	// name, known by the user, as opposed to the name returned by
	// {@link IRodinElement#getElementName()}, which is of a rather internal
	// scope.
	private final String name;
	private final IInternalElement element;
//	private final IDeclaration declaration;
	private final Set<IOccurrence> occurrences;

	public Descriptor(IInternalElement element, String name) {
		this.name = name;
		this.element = element;
//		this.declaration = declaration;
		this.occurrences = new HashSet<IOccurrence>();
	}

//	public Descriptor(IDeclaration declaration) {
//		this.declaration = declaration;
//		this.occurrences = new HashSet<IOccurrence>();
//	}

//	// TODO use instead of getName and getElement
//	public IDeclaration getDeclaration() {
//		return declaration;
//	}
	
	public String getName() {
		return name;
	}

	public IInternalElement getElement() {
		return element;
	}

	public IOccurrence[] getOccurrences() {
		return occurrences.toArray(new Occurrence[occurrences.size()]);
	}

	public boolean hasOccurrence(IOccurrence occurrence) {
		return occurrences.contains(occurrence);
	}

	public void addOccurrence(IOccurrence occurrence) {
		occurrences.add(occurrence);
	}

	public void removeOccurrences(IRodinFile file) {
		final Iterator<IOccurrence> iter = occurrences.iterator();
		while (iter.hasNext()) {
			final IOccurrence occ = iter.next();
			if (file.equals(occ.getRodinFile())) {
				iter.remove();
			}
		}
	}

	// For debugging purposes
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("*** descriptor: ");
		sb.append(element.getElementName() + "\n");
		sb.append("Name: " + name + "\n");
		for (IOccurrence ref : occurrences) {
			sb.append(ref.toString() + "\n");
		}
		return sb.toString();
	}

}
