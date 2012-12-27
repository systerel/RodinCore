/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.tables;

import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;

public interface IExportTable {

	Set<IDeclaration> get(IRodinFile file);

	Set<IRodinFile> files();

}