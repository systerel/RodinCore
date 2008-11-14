/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence;

import java.io.File;

import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;

/**
 * @author Nicolas Beauger
 * 
 */
public interface IPersistor {

	boolean save(PersistentIndexManager data, File file);

	boolean saveProject(ProjectIndexManager pim, File file);

	boolean restore(File file, PersistentIndexManager data);

	boolean restoreProject(File file, PerProjectPIM pppim);
}
