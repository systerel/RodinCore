/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IEvent;
import org.eventb.core.ISCEvent;

/**
 * @author Laurent Voisin
 */
public class TestEventGuardsAndTheorems extends GenericPredicateTest<IEvent, ISCEvent> {

	@Override
	protected IGenericSCTest<IEvent, ISCEvent> newGeneric() {
		return new GenericEventSCTest(this);
	}

}
