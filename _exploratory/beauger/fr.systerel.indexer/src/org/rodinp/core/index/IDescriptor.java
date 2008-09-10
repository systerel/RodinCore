package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public interface IDescriptor {

	public String getName();

	public IInternalElement getElement();
	
	/**
	 * @return the references
	 */
	public Occurrence[] getOccurrences();
	
	public boolean hasOccurrence(Occurrence occurrence);

	public void addOccurrence(Occurrence occurrence);

	public void removeOccurrence(Occurrence occurrence);

	public void removeOccurrences(IRodinFile file);
	
	public void clearOccurrences();

	//DEBUG
	public String toString();
}