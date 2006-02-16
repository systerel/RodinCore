/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Each proof obligation is associated with a description that
 * explains which are the source elements of a proof obligation.
 * It also contains hints for the (automatic) proof.
 * <p>
 * The description also contains an explicative name for the proof obligation.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPODescription extends IInternalElement {
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poDescription"; //$NON-NLS-1$
	
	String getName();
	IPOSource[] getSources() throws RodinDBException;
	IPOHint[] getHints() throws RodinDBException;
}
