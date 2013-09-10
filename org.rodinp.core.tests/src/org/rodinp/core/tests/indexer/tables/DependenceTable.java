/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer.tables;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IRodinFile;

public class DependenceTable {
	
	private static final IRodinFile[] NO_ELEMENTS = new IRodinFile[0];

	private Map<IRodinFile, IRodinFile[]> table;

	public DependenceTable() {
		table = new HashMap<IRodinFile, IRodinFile[]>();
	}

	/**
	 * Adds a dependence between depends and file arguments, so that
	 * <code>depends</code> dependsOn <code>files</code>.
	 * 
	 * @param depends
	 *            the file that depends on <code>files</code>.
	 * @param files
	 *            the files that <code>depends</code> depends on.
	 * @throws IllegalArgumentException
	 *             when redundancy is detected in <code>files</code> as well
	 *             as when making a file self-dependent.
	 */
	public void put(IRodinFile depends, IRodinFile[] files) {
		assertDependencies(depends, files);

		table.put(depends, files);
	}

	/**
	 * Returns the files that the given file depends on.
	 * 
	 * @param depends
	 * @return the files from which it depends.
	 */
	public IRodinFile[] get(IRodinFile depends) {
		final IRodinFile[] dependencies = table.get(depends);
		if (dependencies == null || dependencies.length == 0) {
			return NO_ELEMENTS;
		}

		return dependencies.clone();
	}

	private void assertDependencies(IRodinFile depends, IRodinFile[] files) {
		final List<IRodinFile> fileList = Arrays.asList(files);
		Set<IRodinFile> filesSet = new HashSet<IRodinFile>(fileList);

		if (filesSet.size() != files.length) {
			throw new IllegalArgumentException(
					"Redundancy in dependence files: " + fileList);
		}
		if (filesSet.contains(depends)) {
			throw new IllegalArgumentException(
					"Trying to add a self-dependence to file: " + depends);
		}
	}

}
