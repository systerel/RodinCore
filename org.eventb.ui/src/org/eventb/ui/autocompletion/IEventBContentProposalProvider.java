/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.rodinp.core.location.IAttributeLocation;

/**
 * The interface of Event-B content proposal providers.
 * 
 * @author Thomas Muller
 * @see IContentProposalProvider
 * @since 2.4
 */
public interface IEventBContentProposalProvider extends
		IContentProposalProvider {

	/**
	 * Sets the location of the proposals to the given location. This is
	 * typically the location of the edited attribute in the internal element.
	 * 
	 * @param location
	 *            the location of the attibute of the internal element to set
	 */
	void setLocation(IAttributeLocation location);

}
