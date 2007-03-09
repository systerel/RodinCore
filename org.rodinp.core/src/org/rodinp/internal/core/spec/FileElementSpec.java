/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.spec;

/**
 * @author Stefan Hallerstede
 *
 */
public class FileElementSpec extends ElementSpec {

	public FileElementSpec(ChildSpec[] childSpecs, ValueSpec[] valueSpecs) {
		super(childSpecs, valueSpecs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.internal.spec.ElementSpec#isRepairable()
	 */
	@Override
	public boolean isRepairable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.internal.spec.ElementSpec#isVacuumable()
	 */
	@Override
	public boolean isVacuumable() {
		// TODO Auto-generated method stub
		return false;
	}

}
