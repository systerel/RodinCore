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

import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class AccuracyInfo extends State implements IAccuracyInfo {
	
	private boolean accurate;
	
	public AccuracyInfo() {
		accurate = true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAccuracyInfo#isAccurate()
	 */
	@Override
	public boolean isAccurate() {
		return accurate;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAccuracyInfo#setNotAccurate()
	 */
	@Override
	public void setNotAccurate() {
		accurate = false;
	}

}
