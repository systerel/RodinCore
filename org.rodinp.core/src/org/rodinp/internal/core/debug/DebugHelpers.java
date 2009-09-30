/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.internal.core.debug;

import org.rodinp.internal.core.indexer.IndexManager;

/**
 * @author Nicolas Beauger
 * @since 1.0
 */
public class DebugHelpers {

	//////////////////////////////////////////////////////////////////////////
	//																		//
	//						  IMPORTANT NOTICE								//
	//																		//
	//	Methods of this class are intended to be used for testing purposes	//
	//	only. Do not call in operational code.								//
	//																		//
	//////////////////////////////////////////////////////////////////////////


	
	public static void disableIndexing() {
		IndexManager.getDefault().disableIndexing();
	}

	public static void enableIndexing() {
		IndexManager.getDefault().enableIndexing();
	}

}
