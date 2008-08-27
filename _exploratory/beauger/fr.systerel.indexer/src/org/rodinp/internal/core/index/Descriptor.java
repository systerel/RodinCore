package org.rodinp.internal.core.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.Occurrence;

public final class Descriptor implements IDescriptor {

	private String name; // intended use: public name, user-known
	private IInternalElement element;
	private Set<Occurrence> references;

	public Descriptor(String name, IInternalElement element) {
		this.name = name;
		this.element = element;
		this.references = new HashSet<Occurrence>();
	}
	
	public String getName()	 {
		return name;
	}
	
	public IInternalElement getElement() {
		return element;
	}

	public Occurrence[] getOccurrences() {
		return references.toArray(new Occurrence[references.size()]);
	}

	public boolean hasOccurrence(Occurrence occurrence) {
		return references.contains(occurrence);
	}

	public void addOccurrence(Occurrence occurrence) {
		references.add(occurrence);
	}

	public void addOccurrences(Occurrence[] occurrences) {
		references.addAll(Arrays.asList(occurrences));
	}

	public void removeOccurrence(Occurrence occurrence) {
		references.remove(occurrence);
	}

	public void removeOccurrences(Occurrence[] refs) {
		references.removeAll(Arrays.asList(refs));
	}

//	public void replaceReference(Occurrence oldRef, Occurrence newRef) {
//		if (!references.contains(newRef)) {
//			if (references.remove(oldRef)) {
//				references.add(newRef);
//			}
//		}
//	}

	public void clearOccurrences() {
		references.clear();
	}

	//DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("*** descriptor: ");
		sb.append(element.getElementName() + "\n");
		
		for (Occurrence ref: references) {
			sb.append(ref.toString() + "\n");
		}
		return sb.toString();
	}

}

// /**
// * Find references matching the given arguments. Every argument can be
// * <code>null</code>, thus broadening the result.
// *
// * @param file
// * file to search in, on <code>null</code> to search in every
// * file.
// * @param kind
// * kind of researched reference, or <code>null</code> to get
// * all found kinds.
// * @return matching references that were found.
// */
// public Occurrence[] findReferences(IRodinFile file, OccurrenceKind kind) {
// return null;
// }
