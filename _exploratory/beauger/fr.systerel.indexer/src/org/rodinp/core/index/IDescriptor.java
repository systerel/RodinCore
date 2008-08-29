package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IDescriptor {

	public String getName();

	public IInternalElement getElement();
	
	/**
	 * @return the references
	 */
	public Occurrence[] getOccurrences();
	
	public boolean hasOccurrence(Occurrence occurrence);

	public void addOccurrence(Occurrence occurrence);

//	public void addOccurrences(Occurrence[] occurrences);

	public void removeOccurrence(Occurrence occurrence);

//	public void removeOccurrences(Occurrence[] occurrences);

	public void clearOccurrences();

	//DEBUG
	public String toString();
}