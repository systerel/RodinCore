/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.pog.IPOGNature;

/**
 * Implementation of IPOGNature.
 * <p>
 * Instances of this class are intended to be created through
 * {@link POGNatureFactory} only.
 * </p>
 * 
 * @author A. Gilles
 */
/* package */ class POGNature implements IPOGNature {

	private final String description;

	public POGNature(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
