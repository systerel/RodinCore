/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eventb.internal.ui.utils.Messages;

/**
 * Default master input set to the IManagedForm when creating the master part.
 * Can be set later from outside the class, when the current input is no more
 * accurate, and no other relevant input can be found.
 * <p>
 * It is a singleton implementation, thus static method
 * <code>getDefault()</code> must be called.
 * 
 * @author Nicolas Beauger
 * 
 */
public final class DefaultMasterInput implements IPrfSklInput {
	private static DefaultMasterInput instance;

	private DefaultMasterInput() {
		// Singleton
	}

	public static DefaultMasterInput getDefault() {
		if (instance == null) {
			instance = new DefaultMasterInput();
		}
		return instance;
	}

	@Override
	public String toString() {
		return Messages.proofskeleton_noproof;
	}
}