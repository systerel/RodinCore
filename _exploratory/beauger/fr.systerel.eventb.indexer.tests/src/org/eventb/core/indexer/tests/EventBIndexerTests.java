/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer.tests;

import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.internal.core.index.IndexManager;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class EventBIndexerTests extends AbstractRodinDBTests {

	/**
	 * @param name
	 */
	@SuppressWarnings("restriction")
	public EventBIndexerTests(String name) {
		super(name);
		IndexManager.getDefault().disableIndexing();
	}

}
