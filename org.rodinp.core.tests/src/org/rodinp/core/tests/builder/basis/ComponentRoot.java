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
 *******************************************************************************/
package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.runtime.IPath;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.builder.IContextRoot;
import org.rodinp.core.tests.builder.IDependency;
import org.rodinp.core.tests.builder.IPORoot;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ComponentRoot extends InternalElement {

	protected ComponentRoot(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	public IInternalElement getAlternateVersion(String ext) {
		IPath path = this.getPath().removeFileExtension().addFileExtension(ext);
		String checkedName = path.lastSegment();
		IRodinFile result = getRodinProject().getRodinFile(checkedName);
		return result.getRoot();
	}

	public IContextRoot[] getUsedContexts() throws RodinDBException {
		IDependency[] deps = this.getChildrenOfType(IDependency.ELEMENT_TYPE);
		IContextRoot[] result = new IContextRoot[deps.length];
		final IRodinProject rodinProject = getRodinProject();
		for (int i = 0; i < deps.length; i++) {
			String depName = deps[i].getElementName();
			final IRodinFile ctx = rodinProject.getRodinFile(depName
					+ ".ctx");
			result[i] = (IContextRoot) ctx.getRoot();
		}
		return result;
	}

	public IPORoot getPORoot() {
		return (IPORoot) getAlternateVersion("po");
	}

}
