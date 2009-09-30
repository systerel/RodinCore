/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.internal.core.tool.types.IState;

/**
 * This class is used to mark components of the proof obligation generator state.
 * <p>
 * Clients that need to contribute to the POG state repository {@link IPOGStateRepository}
 * must implement this interface for all contributed state components.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPOGState extends IState {
//	 marker class for proof obligation generator state
}
