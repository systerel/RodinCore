/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.sc.state.IContextAccuracyInfo;
import org.eventb.core.tool.IStateType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAccuracyInfo extends AccuracyInfo implements
		IContextAccuracyInfo {

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}
