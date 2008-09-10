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

	private Map<IRodinFile, Set<IRodinFile>> table;

	public DependenceTable() {
		table = new HashMap<IRodinFile, Set<IRodinFile>>();
	}

	/**
	 * Adds a dependence between depends and file arguments, so that 
	 * depends dependsOn file.
	 * 
	 * @param depends the file that depends on the other ones.
	 * @param files the files that the other one depends on.
	 */
	public void put(IRodinFile depends, IRodinFile[] files) {
		Set<IRodinFile> set = table.get(depends);
		if (set == null) {
			set = new HashSet<IRodinFile>();
			table.put(depends, set);
		}
		set.clear();
		set.addAll(Arrays.asList(files));
	}
	
	/**
	 * Returns the files that the given file depends on.
	 * 
	 * @param depends
	 * @return the files from which it depends.
	 */
	public IRodinFile[] get(IRodinFile depends) {
		final Set<IRodinFile> set = table.get(depends);
		if (set == null || set.isEmpty()) {
			return NO_ELEMENTS;
		}
		return set.toArray(new IRodinFile[set.size()]);
	}

	public IRodinFile[] getDependents(IRodinFile file) {
		// TODO costly: consider caching a reverse table.
		List<IRodinFile> result = new ArrayList<IRodinFile>();
		
		for (IRodinFile f : table.keySet()) {
			if (table.get(f).contains(file)) {
				result.add(f);
			}
		}
		return result.toArray(new IRodinFile[result.size()]);
	}
	
	public void remove(IRodinFile depends) {
		table.remove(depends);
	}
	
	public void clear() {
		table.clear();
	}
	
}
