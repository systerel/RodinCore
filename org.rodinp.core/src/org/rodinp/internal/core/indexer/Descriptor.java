/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;

public final class Descriptor {

	private final IDeclaration declaration;
	private final Set<IOccurrence> occurrences;

	public Descriptor(IDeclaration declaration) {
		this.declaration = declaration;
		this.occurrences = new HashSet<IOccurrence>();
	}

	public IDeclaration getDeclaration() {
		return declaration;
	}

	public Set<IOccurrence> getOccurrences() {
		return Collections.unmodifiableSet(occurrences);
	}

	public boolean hasOccurrence(IOccurrence occurrence) {
		return occurrences.contains(occurrence);
	}

	public boolean isEmpty() {
		return occurrences.isEmpty();
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
		sb.append(declaration + "\n");
		for (IOccurrence occ : occurrences) {
			sb.append(occ + "\n");
		}
		return sb.toString();
	}

}
