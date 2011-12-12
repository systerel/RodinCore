/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
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

	void setLocation(IAttributeLocation location);

}
