/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class TestEventGuardsAndTheorems extends GenericPredicateTest<IEvent> {

	@Override
	protected IGenericPOTest<IEvent> newGeneric() {
		return new GenericEventPOTest(this);
	}

	@Override
	public String getTHMPOName(IEvent event, String predName)
			throws RodinDBException {
		return getPOPrefix(event, predName) + "/THM";
	}

	@Override
	public String getWDPOName(IEvent event, String predName)
			throws RodinDBException {
		return getPOPrefix(event, predName) + "/WD";
	}

	private String getPOPrefix(IEvent event, String predName)
	throws RodinDBException {
		return event.getLabel() + "/" + predName;
	}
	
	@Override
	public boolean isCumulative() {
		return false;
	}

}
