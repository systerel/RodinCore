/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.internal.core.tool.types.IState;

/**
 * All static checker state components must inherit from this interface.
 * <p>
 * Clients that need to contribute to the SC state repository {@link ISCStateRepository}
 * must implement this interface for all contributed state components.
 * </p>
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface ISCState extends IState {
  // marker class for static checker state
}
