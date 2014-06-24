/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.FormulaExtensionProviderRegistry.getExtensionProviderRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of a variant of the Event-B mathematical language. This is
 * done by storing explicitly a formula factory in the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ILanguage</code>.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 3.0
 */
public class Language extends InternalElement implements ILanguage {

	public Language(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public FormulaFactory getFormulaFactory(IProgressMonitor monitor)
			throws CoreException {
		return getExtensionProviderRegistry().getFormulaFactory(this, monitor);
	}

	@Override
	public void setFormulaFactory(FormulaFactory factory, IProgressMonitor pm)
			throws RodinDBException {
		final SubMonitor sm = SubMonitor.convert(pm, 2);
		try {
			clear(false, sm.newChild(1));
			getExtensionProviderRegistry().setFormulaFactory(this, factory,
					sm.newChild(1));
		} finally {
			if (pm != null) {
				pm.done();
			}
		}
	}

}
