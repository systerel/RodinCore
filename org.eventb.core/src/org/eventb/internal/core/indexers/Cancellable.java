/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import java.util.concurrent.CancellationException;

import org.rodinp.core.indexer.IIndexingBridge;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class Cancellable {

	protected static void checkCancel(IIndexingBridge bridge) {
		if (bridge.isCancelled()) {
			throw new CancellationException();
		}
	}

}
