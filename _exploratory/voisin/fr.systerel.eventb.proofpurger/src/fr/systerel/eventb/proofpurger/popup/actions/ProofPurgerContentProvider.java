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
package fr.systerel.eventb.proofpurger.popup.actions;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;

/**
 * Content provider for the proof purger.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofPurgerContentProvider implements ITreeContentProvider {

	private static final Object[] NO_OBJECTS = new Object[0];
	
	private final Set<IPRProof> prProofs;
	private final Map<IPRFile, LinkedHashSet<IPRProof>> mapFileProofs;
	private final Map<IRodinProject, LinkedHashSet<IPRFile>> mapProjectFiles;

	/**
	 * Constructor. Initializes its underlying tree structure from the given
	 * proofs.
	 * 
	 * @param proofs
	 *            The proofs that will be provided.
	 */
	public ProofPurgerContentProvider(IPRProof[] proofs) {
		this.prProofs = new LinkedHashSet<IPRProof>();
		this.mapFileProofs = new LinkedHashMap<IPRFile, LinkedHashSet<IPRProof>>();
		this.mapProjectFiles = new LinkedHashMap<IRodinProject, LinkedHashSet<IPRFile>>();

		updateFromProofs(proofs);
	}

	public void dispose() {
		// Do nothing
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Do nothing
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof IRodinDB) {
			final Set<IRodinProject> projects = mapProjectFiles.keySet();
			return projects.toArray(new IRodinProject[projects.size()]);
		}
		if (parent instanceof IRodinProject) {
			LinkedHashSet<IPRFile> prFiles = mapProjectFiles.get(parent);
			if (prFiles == null) {
				return NO_OBJECTS;
			}
			return prFiles.toArray(new IPRFile[prFiles.size()]);
		}
		if (parent instanceof IPRFile) {
			LinkedHashSet<IPRProof> proofs = mapFileProofs.get(parent);
			if (proofs == null) {
				return NO_OBJECTS;
			}
			return proofs.toArray(new IPRProof[proofs.size()]);
		}
		return NO_OBJECTS;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IRodinDB) {
			return true;
		}
		if (element instanceof IRodinProject) {
			return mapProjectFiles.containsKey(element);
		}
		if (element instanceof IPRFile) {
			return mapFileProofs.containsKey(element);
		}
		return false;
	}

	public Object getParent(Object child) {
		if (child instanceof IRodinProject) {
			return ((IRodinProject) child).getParent();
		}
		if (child instanceof IPRFile) {
			return ((IPRFile) child).getParent();
		}
		if (child instanceof IPRProof) {
			return ((IPRProof) child).getParent();
		}
		return null;
	}
	
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	private void updateFromProofs(IPRProof[] proofs) {
		updateProofs(proofs);
		updateFiles();
		updateProjects();
	}

	private void updateProofs(IPRProof[] proofs) {
		prProofs.clear();
		for (IPRProof pr: proofs) {
			prProofs.add(pr);
		}
	}
	
	private <T extends Object, U extends Object> void updateMap(Map<T, LinkedHashSet<U>> map, T t, U u) {
		LinkedHashSet<U> currentSet = map.get(t);
		if (currentSet == null) {
			currentSet = new LinkedHashSet<U>();
			currentSet.add(u);
			map.put(t, currentSet);
		} else {
			currentSet.add(u);
		}
		
	}

	private void updateFiles() {
		mapFileProofs.clear();
		for (IPRProof pr: prProofs) {
			final IPRFile prFile = (IPRFile) pr.getRodinFile();
			updateMap(mapFileProofs, prFile, pr);
		}
	}

	private void updateProjects() {
		mapProjectFiles.clear();
		for (IPRFile f: mapFileProofs.keySet()) {
			final IRodinProject prj = f.getRodinProject();
			updateMap(mapProjectFiles, prj, f);
		}
	}

}
