/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.spec;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.spec.IElementSpec;
import org.rodinp.core.spec.SpecProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ElementSpec implements IElementSpec {
	
	private final ChildSpec[] childSpecs;
	private final ValueSpec[] valueSpecs;

	public ElementSpec(final ChildSpec[] childSpecs, final ValueSpec[] valueSpecs) {
		super();
		this.childSpecs = childSpecs;
		this.valueSpecs = valueSpecs;
	}

	@Override
	public IElementType<? extends IRodinElement> getElementType() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract boolean isRepairable();

	public abstract boolean isVacuumable();

	@Override
	public void repair(IRodinElement element, boolean recursive, IProgressMonitor monitor) 
	throws RodinDBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vacuum(IRodinElement element, boolean recursive, IProgressMonitor monitor) 
	throws RodinDBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SpecProblem> verify(IRodinElement element, boolean recursive) 
	throws RodinDBException {
		// TODO Auto-generated method stub
		return null;
	}

}
