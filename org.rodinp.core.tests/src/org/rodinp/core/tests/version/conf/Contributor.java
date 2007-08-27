/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.IContributor;

/**
 * @author Stefan Hallerstede
 *
 */
public class Contributor implements IContributor {

	private final String bundle;
	
	public Contributor(String bundle) {
		this.bundle = bundle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IContributor#getName()
	 */
	public String getName() {
		return bundle;
	}

}
