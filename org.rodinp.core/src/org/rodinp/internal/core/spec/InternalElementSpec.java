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
package org.rodinp.internal.core.spec;

/**
 * @author Stefan Hallerstede
 *
 */
public class InternalElementSpec extends ElementSpec {

	public InternalElementSpec(ChildSpec[] childSpecs, ValueSpec[] valueSpecs) {
		super(childSpecs, valueSpecs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isRepairable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVacuumable() {
		// TODO Auto-generated method stub
		return false;
	}

}
