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

import org.eventb.core.IEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IPropagator;

/**
 * @author Nicolas Beauger
 * 
 */
public class EventPropagator implements IPropagator {

	private static final IPropagator instance = new EventPropagator();

	private EventPropagator() {
		// singleton: private constructor
	}

	public static IPropagator getDefault() {
		return instance;
	}

	// Assumption: an event redeclaration occurs in a descendant of the concrete
	// event
	public IDeclaration getRelativeDeclaration(IOccurrence occurrence,
			IIndexQuery query) {
		ensureEventDeclaration(occurrence);
		if (occurrence.getKind() != REDECLARATION) {
			return null;
		}
		final IInternalElement occElem = occurrence.getLocation().getElement();
		final IEvent relativeEvent = occElem.getAncestor(IEvent.ELEMENT_TYPE);
		return query.getDeclaration(relativeEvent);
	}

	private void ensureEventDeclaration(IOccurrence occurrence) {
		final IDeclaration decl = occurrence.getDeclaration();
		final IInternalElement declElem = decl.getElement();
		if (declElem.getElementType() != IEvent.ELEMENT_TYPE) {
			throw new IllegalArgumentException("Not an event occurrence");
		}
	}

}
