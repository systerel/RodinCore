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
package org.rodinp.core.index;

import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.IndexManager;

/**
 * <em>Temporary class</em>
 * 
 * Main entry point for the Rodin indexer. Static methods of this class will
 * later be moved to {@link RodinCore}.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RodinIndexer {

	/** To be moved to {@link IRodinDBStatusConstants} */
	public static final int INVALID_LOCATION = 999;

	/** To be moved to {@link RodinCore} */
	public static final void register(IIndexer indexer) {
		IndexManager.getDefault().addIndexer(indexer);
	}

	/** To be moved to {@link RodinCore} */
	public static final void deregister(IIndexer indexer) {
		IndexManager.getDefault().removeIndexer(indexer);
	}

}
