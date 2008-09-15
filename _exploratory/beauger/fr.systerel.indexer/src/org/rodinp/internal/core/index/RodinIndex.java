package org.rodinp.internal.core.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;

public final class RodinIndex {

	private Map<IInternalElement, Descriptor> map;

	public RodinIndex() {
		map = new HashMap<IInternalElement, Descriptor>();
	}

	/**
	 * Gets the Descriptor corresponding to the given element. Returns
	 * <code>null</code> if such a Descriptor does not exist.
	 * 
	 * @param element
	 * @return the Descriptor of the given element, or <code>null</code> if it
	 *         does not exist.
	 * @see #makeDescriptor(IInternalElement, String)
	 */
	public Descriptor getDescriptor(IInternalElement element) {
		return map.get(element);
	}

	public Descriptor[] getDescriptors() {
		final Collection<Descriptor> descriptors = map.values();
		return descriptors.toArray(new Descriptor[descriptors.size()]);
	}

	/**
	 * Creates a Descriptor for the given element, with the given name. Throws
	 * {@link IllegalArgumentException} if a Descriptor already exists for the
	 * element.
	 * 
	 * @param element
	 *            the element for which to make a Descriptor.
	 * @param name
	 *            the public name (user-known) of the element.
	 * @return the newly created Descriptor.
	 * @throws IllegalArgumentException
	 *             if the Descriptor already exists.
	 * @see #getDescriptor(IInternalElement)
	 */
	public Descriptor makeDescriptor(IInternalElement element, String name) {
		if (map.containsKey(element)) {
			throw new IllegalArgumentException(
					"Descriptor for already exists for element: "
							+ element.getElementName());
		}
		final Descriptor descriptor = new Descriptor(element, name);
		map.put(element, descriptor);

		return descriptor;
	}

	public void removeDescriptor(Object key) {
		map.remove(key);
	}

	public void rename(IInternalElement element, String name) {
		final Descriptor descriptor = map.get(element);

		if (descriptor == null) {
			throw new IllegalArgumentException("The element "
					+ element.getElementName()
					+ " cannot be renamed as it has no descriptor.");
		}
		descriptor.setName(name);
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

}
