package org.rodinp.internal.core.index.tables;

import java.util.ArrayList;
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

	/**
	 * Returns the files that depend on the given one.
	 * 
	 * @param file
	 *            the file from which to get dependents.
	 * @return the dependent files.
	 */
	public IRodinFile[] getDependents(IRodinFile file) {
		List<IRodinFile> result = new ArrayList<IRodinFile>();

		for (IRodinFile f : table.keySet()) {
			if (Arrays.asList(table.get(f)).contains(file)) {
				result.add(f);
			}
		}
		return result.toArray(new IRodinFile[result.size()]);
	}

	public void clear() {
		table.clear();
	}

	private void assertDependencies(IRodinFile depends, IRodinFile[] files) {
		Set<IRodinFile> filesSet = new HashSet<IRodinFile>(Arrays.asList(files));

		if (filesSet.size() != files.length) {
			throw new IllegalArgumentException(
					"Redundancy in dependence files: " + files);
		}
		if (filesSet.contains(depends)) {
			throw new IllegalArgumentException(
					"Trying to add a self-dependence to file: " + depends);
		}
	}

}
