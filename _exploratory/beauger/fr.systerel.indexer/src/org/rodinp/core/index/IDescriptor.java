package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IDescriptor {

	public String getName();

	public IInternalElement getElement();
	
	/**
	 * @return the references
	 */
	public IOccurrence[] getOccurrences();
	
	public boolean hasOccurrence(IOccurrence occurrence);

	//DEBUG
	public String toString();
}