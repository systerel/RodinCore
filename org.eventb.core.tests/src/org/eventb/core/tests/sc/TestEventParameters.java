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
package org.eventb.core.tests.sc;

import org.eventb.core.IEvent;
import org.eventb.core.ISCEvent;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventParameters extends GenericIdentTest<IEvent, ISCEvent> {

	@Override
	protected IGenericSCTest<IEvent, ISCEvent> newGeneric() {
		return new GenericEventSCTest(this);
	}

}
