package org.rodinp.core.index;

import java.util.Collection;

import org.rodinp.core.IInternalElement;

public interface IRodinIndex {

	public IDescriptor getDescriptor(Object key);

	public Collection<IDescriptor> getDescriptors();

	public IDescriptor makeDescriptor(IInternalElement element, String name);
	
	public void removeDescriptor(Object key);
}
