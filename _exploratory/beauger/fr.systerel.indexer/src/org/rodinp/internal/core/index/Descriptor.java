package org.rodinp.internal.core.index;

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;

public final class Descriptor {

	/**
	 * Name of the described element. It is intended to be used as a public
	 * name, known by the user, as opposed to the name returned by
	 * {@link IRodinElement#getElementName()}, which is of a rather internal
	 * scope.
	 */
	private String name;
	private IInternalElement element;
	private Set<Occurrence> occurrences;

	public Descriptor(IInternalElement element, String name) {
		this.name = name;
		this.element = element;
		this.occurrences = new HashSet<Occurrence>();
	}

	public String getName() {
		return name;
	}

	public IInternalElement getElement() {
		return element;
	}

	public Occurrence[] getOccurrences() {
		return occurrences.toArray(new Occurrence[occurrences.size()]);
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasOccurrence(Occurrence occurrence) {
		return occurrences.contains(occurrence);
	}

	public void addOccurrence(Occurrence occurrence) {
		occurrences.add(occurrence);
	}

	public void removeOccurrences(IRodinFile file) {
		final Set<Occurrence> toRemove = new HashSet<Occurrence>();
		for (Occurrence occ : occurrences) {
			if (isInSameFile(occ.getLocation(), file)) {
				toRemove.add(occ);
			}
		}
		occurrences.removeAll(toRemove);
	}

	private boolean isInSameFile(IRodinLocation location, IRodinFile file) {
		final IRodinElement locElem = location.getElement();
		if (locElem instanceof IRodinFile) {
			return locElem == file;
		} else if (locElem instanceof IInternalElement) {
			return ((IInternalElement) locElem).getRodinFile() == file;
		}
		return false;
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("*** descriptor: ");
		sb.append(element.getElementName() + "\n");
		sb.append("Name: " + name + "\n");

		for (Occurrence ref : occurrences) {
			sb.append(ref.toString() + "\n");
		}
		return sb.toString();
	}

}
