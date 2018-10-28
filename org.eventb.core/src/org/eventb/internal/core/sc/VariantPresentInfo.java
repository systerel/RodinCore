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
import org.eventb.core.sc.state.IVariantPresentInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @see IVariantPresentInfo
 * @author Laurent Voisin
 */
public class VariantPresentInfo extends State implements IVariantPresentInfo {

	private boolean present;

	@Override
	public boolean isTrue() throws CoreException {
		assertImmutable();
		return present;
	}

	@Override
	public void set(boolean present) throws CoreException {
		assertMutable();
		this.present = present;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public String toString() {
		return present ? "variantIsPresent" : "variantIsNotPresent";
	}

}
