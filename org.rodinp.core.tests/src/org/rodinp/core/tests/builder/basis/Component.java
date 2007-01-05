/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.core.tests.builder.IContext;
import org.rodinp.core.tests.builder.IDependency;
import org.rodinp.core.tests.builder.IPOFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Component extends RodinFile {

	protected Component(IFile file, IRodinElement parent) {
		super(file, parent);
	}
	
	public IRodinFile getAlternateVersion(String ext) {
		IPath path = this.getPath().removeFileExtension().addFileExtension(ext);
		String checkedName = path.lastSegment();
		IRodinFile result = ((IRodinProject) this.getParent()).getRodinFile(checkedName);
		return result;
	}

	public IContext[] getUsedContexts() throws RodinDBException {
		IDependency[] deps = this.getChildrenOfType(IDependency.ELEMENT_TYPE);
		IContext[] result = new IContext[deps.length];
		final IRodinProject rodinProject = getRodinProject();
		for (int i = 0; i < deps.length; i++) {
			String depName = deps[i].getElementName();
			result[i] = (IContext) rodinProject.getRodinFile(depName + ".ctx");
		}
		return result;
	}

	public IPOFile getPOFile() {
		return (IPOFile) getAlternateVersion("po");
	}

}
