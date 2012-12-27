/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;

public interface IIndexingResult {

	Collection<IDeclaration> getDeclarations();

	Set<IDeclaration> getExports();

	Map<IInternalElement, Set<IOccurrence>> getOccurrences();

	IRodinFile getFile();

	boolean isSuccess();

}