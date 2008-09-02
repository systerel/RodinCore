package org.rodinp.core.index;


import org.rodinp.core.IInternalElement;

public interface IRodinIndex {

	public IDescriptor getDescriptor(Object key);

	public IDescriptor[] getDescriptors();

	public IDescriptor makeDescriptor(IInternalElement element, String name);
	
	public void removeDescriptor(Object key);
	
	public void clear();
}
