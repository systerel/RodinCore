/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

/**
 * Common superclass for all info classes of the tactic registry. Provides just
 * an id.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractInfo {

	protected final String id;

	public AbstractInfo(String id) {
		this.id = id;
	}

	public final String getID() {
		return id;
	}

}