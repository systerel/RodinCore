/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence;

import java.io.File;

import org.rodinp.internal.core.indexer.PerProjectPIM;

/**
 * @author Nicolas Beauger
 */
// TODO consider using org.eclipse.ui.IPersistable/IPersistableElement and
// IMemento interfaces
public interface IPersistor {

	boolean save(PersistentIndexManager data, File file);

	boolean saveProject(PersistentPIM pim, File file);

	boolean restore(File file, PersistentIndexManager data);

	boolean restoreProject(File file, PerProjectPIM pppim);
}
