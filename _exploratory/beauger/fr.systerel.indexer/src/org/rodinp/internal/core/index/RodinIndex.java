package org.rodinp.internal.core.index;

import java.util.Collection;
import java.util.HashMap;

import org.rodinp.core.IInternalElement;

public final class RodinIndex {

	private HashMap<Object, Descriptor> map;

	public RodinIndex() {
		map = new HashMap<Object, Descriptor>();
	}

	// FIXME perhaps we do not really want to have null returned
	public Descriptor getDescriptor(Object key) {
		return map.get(key);
	}

	public Descriptor[] getDescriptors() {
		final Collection<Descriptor> descriptors = map.values();
		return descriptors.toArray(new Descriptor[descriptors.size()]);
	}

	public Descriptor makeDescriptor(IInternalElement element, String name) {
		Descriptor descriptor = map.get(element);
		if (descriptor == null) {
			descriptor = new Descriptor(name, element);
			map.put(element, descriptor);
		} else { // requesting to make an already existing descriptor
			if (descriptor.getElement() != element
					|| !descriptor.getName().equals(name)) {
				throw new IllegalArgumentException("Descriptor for "
						+ element.getElementName()
						+ " already exists with a different name");
			}
			// else return the already existing one
			// as it is coherent with the requested one
		}
		return descriptor;
	}

	public void removeDescriptor(Object key) {
		map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("index\n");
		for (Object o : map.keySet()) {
			sb.append(map.get(o).toString() + "\n");
		}
		return sb.toString();
	}

	public boolean isDeclared(IInternalElement element) {
		return map.containsKey(element);
	}

}
