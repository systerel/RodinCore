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
package org.eventb.internal.core.sc;

import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.tool.IStateType;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineAccuracyInfo extends AccuracyInfo implements
		IMachineAccuracyInfo {

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
