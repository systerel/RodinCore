/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.part.IPage;
import org.rodinp.core.IElementChangedListener;

/**
 * @author htson
 *         <p>
 *         Common interface for Event-B mirror pages. This extends the IPage
 *         interface and element changed listener.
 */
public interface IEventBMirrorPage extends IPage, IElementChangedListener {
	// Only extend IPage and IElementChangedListener
}
