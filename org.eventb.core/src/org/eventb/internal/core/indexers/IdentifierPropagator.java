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
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class IdentifierPropagator implements IPropagator {

	private static IPropagator instance;

	private IdentifierPropagator() {
		// singleton: private constructor
	}

	public static IPropagator getDefault() {
		if (instance == null) {
			instance = new IdentifierPropagator();
		}
		return instance;
	}

	// assumption : identifier redeclaration occurs in the identifier attribute
	// of the redeclaring identifier
	public IDeclaration getRelativeDeclaration(IOccurrence occurrence,
			IIndexQuery query) {
		if (!(occurrence.getDeclaration().getElement() instanceof IIdentifierElement)) {
			throw new IllegalArgumentException(
					"Should be called on identifier occurrences");
		}
		if (!occurrence.getKind().equals(REDECLARATION)) {
			return null;
		}
		final IInternalLocation location = occurrence.getLocation();
		final IInternalElement occElem = location.getElement();
		return query.getDeclaration(occElem);
	}

	protected boolean sameElementType(IInternalElement elt1,
			IInternalElement elt2) {
		return elt1.getElementType() == elt2.getElementType();
	}

}
