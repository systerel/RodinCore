/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog.modules;

import org.eventb.core.pog.POGFilterModule;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class POGFilterTestModule extends POGFilterModule {

	protected static String getPluginID() {
		return "org.eventb.core.tests";
	}

}
