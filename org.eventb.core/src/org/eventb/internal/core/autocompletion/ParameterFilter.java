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
package org.eventb.internal.core.autocompletion;

import org.eventb.core.IEvent;
import org.eventb.core.IParameter;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ParameterFilter extends AbstractFilter {

	protected final IEvent event;

	public ParameterFilter(IEvent event) {
		this.event = event;
	}

	@Override
	protected boolean propose(IDeclaration declaration) {
		final IInternalElement element = declaration.getElement();
		if (!(element instanceof IParameter)) {
			return false;
		}
		final IEvent parentEvent = element.getAncestor(
				IEvent.ELEMENT_TYPE);
		return event.equals(parentEvent);
	}


}
