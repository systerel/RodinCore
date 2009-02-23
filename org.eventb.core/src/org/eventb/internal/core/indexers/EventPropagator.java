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

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IPropagator;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class EventPropagator extends EventBPropagator {

	private static IPropagator instance;

	private EventPropagator() {
		// singleton: private constructor
	}

	public static IPropagator getDefault() {
		if (instance == null) {
			instance = new EventPropagator();
		}
		return instance;
	}

	public IDeclaration getRelativeDeclaration(IOccurrence occurrence,
			IIndexQuery query) {
		final IInternalElement element = occurrence.getDeclaration()
				.getElement();
		final IInternalLocation location = occurrence.getLocation();
		if (sameFile(location, element)) {
			return null;
		}
		if (!(location instanceof IAttributeLocation)) {
			return null;
		}
		IAttributeLocation attLoc = (IAttributeLocation) location;
		final IInternalElement occElem = attLoc.getElement();
		if (occElem.getElementType() != IRefinesEvent.ELEMENT_TYPE) {
			return null;
		}
		if (!hasAttributeType(attLoc, TARGET_ATTRIBUTE)) {
			return null;
		}
		final IInternalElement relativeEvent = (IInternalElement) occElem
				.getParent();
		return query.getDeclaration(relativeEvent);
	}

}
