/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IRodinProject;

/**
 * @author Nicolas Beauger
 * 
 */
public class PerProjectPIM {

	private final Map<IRodinProject, ProjectIndexManager> pims;

	public PerProjectPIM() {
		this.pims = new LinkedHashMap<IRodinProject, ProjectIndexManager>();
	}

	public Set<IRodinProject> projects() {
		return pims.keySet();
	}

	public Collection<ProjectIndexManager> pims() {
		return pims.values();
	}

	public ProjectIndexManager get(IRodinProject project) {
		return pims.get(project);
	}

	public ProjectIndexManager getOrCreate(IRodinProject project) {
		ProjectIndexManager pim = pims.get(project);
		if (pim == null) {
			pim = new ProjectIndexManager(project);
			pims.put(project, pim);
		}
		return pim;
	}

	public void put(ProjectIndexManager pim) {
		pims.put(pim.getProject(), pim);
	}

	public void remove(IRodinProject project) {
		pims.remove(project);
	}

	public void clear() {
		pims.clear();
	}
}
