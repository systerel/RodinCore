/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.IVariantUsedInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @see IVariantUsedInfo
 * @author Laurent Voisin
 */
public class VariantUsedInfo extends State implements IVariantUsedInfo {

	private boolean used;

	@Override
	public boolean isTrue() throws CoreException {
		assertImmutable();
		return used;
	}

	@Override
	public void set(boolean used) throws CoreException {
		assertMutable();
		this.used = used;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public String toString() {
		return used ? "variantIsUsed" : "variantIsNotUsed";
	}

}
