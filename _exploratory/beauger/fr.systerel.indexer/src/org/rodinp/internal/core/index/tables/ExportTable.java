package org.rodinp.internal.core.index.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public class ExportTable {

	Map<IRodinFile, Set<IInternalElement>> table;

	public ExportTable() {
		table = new HashMap<IRodinFile, Set<IInternalElement>>();
	}

	public Set<IInternalElement> get(IRodinFile file) {
		final Set<IInternalElement> set = table.get(file);
		if (set == null) {
			return new HashSet<IInternalElement>();
		}
		return new HashSet<IInternalElement>(set);
	}

	/**
	 * Overwrites any previous mapping from the given file to the element, and
	 * from the given element to the name.
	 * 
	 * @param file
	 * @param element
	 */
	public void add(IRodinFile file, IInternalElement element) {
		Set<IInternalElement> set = table.get(file);
		if (set == null) {
			set = new HashSet<IInternalElement>();
			table.put(file, set);
		}
		set.add(element);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

}
