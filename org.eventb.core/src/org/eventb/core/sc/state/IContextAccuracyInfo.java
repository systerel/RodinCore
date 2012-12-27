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
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * The accuracy info for the current context.
 * 
 * @see IAccuracyInfo
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IContextAccuracyInfo extends IAccuracyInfo {
	
	final static IStateType<IContextAccuracyInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextAccuracyInfo");

}
