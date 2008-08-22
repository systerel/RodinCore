package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IDescriptor {

	public String getName();

	public IInternalElement getElement();
	
	/**
	 * @return the references
	 */
	public Occurrence[] getOccurrences();
	
	public boolean hasOccurrence(Occurrence ref);

	public void addOccurrence(Occurrence newRef);

	public void addOccurrences(Occurrence[] newRefs);

	public void removeOccurrence(Occurrence ref);

	public void removeOccurrences(Occurrence[] refs);

	public void clearOccurrences();

	//DEBUG
	public String toString();
}