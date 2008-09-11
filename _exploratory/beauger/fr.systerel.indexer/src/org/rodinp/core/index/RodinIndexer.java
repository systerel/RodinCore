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

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.RodinLocation;

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
	public static final void register(IIndexer indexer, IFileElementType<?> fileType) {
		IndexManager.getDefault().addIndexer(indexer, fileType);
	}
	
	public static IRodinLocation getRodinLocation(IRodinElement element) {
		return getRodinLocation(element, null);
	}

	public static IRodinLocation getRodinLocation(IRodinElement element,
			IAttributeType attributeType) {
		return getRodinLocation(element, attributeType,
				IRodinLocation.NULL_CHAR_POS, IRodinLocation.NULL_CHAR_POS);
	}

	public static IRodinLocation getRodinLocation(IRodinElement element,
			IAttributeType attributeType, int start, int end) {
		return new RodinLocation(element, attributeType, start, end);
	}

}
