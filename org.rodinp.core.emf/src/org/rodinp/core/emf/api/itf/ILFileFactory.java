/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.emf.lightcore.RodinResourceFactory;

/**
 * @author "Thomas Muller"
 */
public interface ILFileFactory {
	
	ILFileFactory INSTANCE = new RodinResourceFactory();
	
	ILFile createILFile(IFile file);
	
	void removeILFile(ILFile file);
	
}
