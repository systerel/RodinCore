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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IPropagator;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class EventBPropagator implements IPropagator {

	protected boolean hasAttributeType(IAttributeLocation attLoc, IAttributeType.String attributeType) {
		return attLoc.getAttributeType() == attributeType;
	}

	protected boolean sameFile(IInternalLocation location, IInternalElement element) {
		return location.getRodinFile().equals(element.getRodinFile());
	}

}
