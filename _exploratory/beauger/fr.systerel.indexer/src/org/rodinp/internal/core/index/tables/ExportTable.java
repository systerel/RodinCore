package org.rodinp.internal.core.index.tables;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public class ExportTable {

	Map<IRodinFile, Map<IInternalElement, String>> table;

	public ExportTable() {
		table = new HashMap<IRodinFile, Map<IInternalElement, String>>();
	}

	// TODO consider providing a type that hides the map
	public Map<IInternalElement, String> get(IRodinFile file) {
		final Map<IInternalElement, String> map = table.get(file);
		if (map == null) {
			return new HashMap<IInternalElement, String>();
		}
		return new HashMap<IInternalElement, String>(map);
	}

	public void put(IRodinFile file, Map<IInternalElement, String> elements) {
		table.put(file, elements);
	}
	
	public void remove(IRodinFile file) {
		table.remove(file);
	}
	
	public void clear() {
		table.clear();
	}
	
}
