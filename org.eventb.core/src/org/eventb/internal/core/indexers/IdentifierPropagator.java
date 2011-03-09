/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBPlugin.REDECLARATION;

import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IPropagator;

/**
 * @author Nicolas Beauger
 * 
 */
public class IdentifierPropagator implements IPropagator {

	private static IPropagator instance = new IdentifierPropagator();

	private IdentifierPropagator() {
		// singleton: private constructor
	}

	public static IPropagator getDefault() {
		return instance;
	}

	// Assumption : an identifier redeclaration occurs in an attribute
	// of the redeclaring identifier
	@Override
	public IDeclaration getRelativeDeclaration(IOccurrence occurrence,
			IIndexQuery query) {
		ensureIdentifierDeclaration(occurrence);
		if (occurrence.getKind() != REDECLARATION) {
			return null;
		}
		return query.getDeclaration(occurrence.getLocation().getElement());
	}

	private void ensureIdentifierDeclaration(IOccurrence occurrence) {
		final IDeclaration declaration = occurrence.getDeclaration();
		final IInternalElement declElem = declaration.getElement();
		if (!(declElem instanceof IIdentifierElement)) {
			throw new IllegalArgumentException("Not an identifier occurrence");
		}
	}

}
