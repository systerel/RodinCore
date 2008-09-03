package org.rodinp.internal.core.index.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;

public class NameTable {

	private static final IInternalElement[] NO_ELEMENTS = new IInternalElement[0];

	private Map<String, Set<IInternalElement>> map;
	
	public NameTable() {
		map = new HashMap<String, Set<IInternalElement>>();
	}
	
	public void put(String name, IInternalElement element) {
		Set<IInternalElement> elements = map.get(name);
		if (elements == null) {
			elements = new HashSet<IInternalElement>();
			map.put(name, elements);
		}
		elements.add(element);
	}
	
	public void remove(String name, IInternalElement element) {
		Set<IInternalElement> elements = map.get(name);
		if (elements != null) {
			elements.remove(element);
			if (elements.size() == 0) {
				map.remove(name);
			}
		}
	}
	
	public IInternalElement[] getElements(String name) {
		final Set<IInternalElement> elements = map.get(name);
		if (elements == null || elements.size() == 0) {
			return NO_ELEMENTS;
		}
		return elements.toArray(new IInternalElement[elements.size()]);
	}
	
	public void clear() {
		map.clear();
	}
	
	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("NameTable\n");
		for (String name : map.keySet()) {
			sb.append(name + ": ");
			for (IInternalElement elem : map.get(name)) {
				sb.append(elem.getElementName()+"; ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
