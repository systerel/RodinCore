package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IRodinIndex {

	public IDescriptor getDescriptor(Object key);

	public IDescriptor makeDescriptor(String elementName, IInternalElement element, Object key);
	
	public void removeDescriptor(Object key);
}
