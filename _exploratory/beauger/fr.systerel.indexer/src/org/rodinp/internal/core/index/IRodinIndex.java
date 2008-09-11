package org.rodinp.internal.core.index;


import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDescriptor;

public interface IRodinIndex {

	boolean isDeclared(IInternalElement element);
	
	public IDescriptor getDescriptor(Object key);
	// FIXME why not directly getDescriptor(IInternalElement), just like everywhere else ?

	public IDescriptor[] getDescriptors();

	public IDescriptor makeDescriptor(IInternalElement element, String name);
	
	public void removeDescriptor(Object key);
	
	public void clear();
}
