package org.rodinp.internal.core.index;

import java.util.HashMap;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IRodinIndex;

public final class RodinIndex implements
		IRodinIndex {

	private HashMap<Object, IDescriptor> map; 

	public RodinIndex() {
		map = new HashMap<Object, IDescriptor>();
	}

	public IDescriptor getDescriptor(Object key) {
		return map.get(key);
	}

	public IDescriptor makeDescriptor(String elementName,
			IInternalElement element, Object key) {
		IDescriptor descriptor = map.get(key);
		if (descriptor == null) {
			descriptor = new Descriptor(elementName, element);
			map.put(key, descriptor);
		} else { // requesting to make an already existing descriptor
			if (descriptor.getElement() != element
					|| !descriptor.getName().equals(elementName)) {
				// TODO: throw an exception
			}
			// else return the already existing one
			// as it is coherent with the requested one
		}
		return descriptor;
	}

	public void removeDescriptor(Object key) {
		map.remove(key);
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

}
