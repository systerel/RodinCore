/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;

/**
 * A hint for a proof of a proof obligation. A hint is labeled so that hints can be
 * accumulated. This is a marker interface to permit systematic treatment of hints
 * in implementations. All hints should extend this interface.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @see IPOSelectionHint
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface IPOHint extends IInternalElement {
	
	// marker interface
}
