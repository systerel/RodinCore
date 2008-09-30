package org.rodinp.internal.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;

public class RodinLocation implements IRodinLocation {

	private final IRodinElement element;

	public RodinLocation(IRodinElement element) {
		if (element == null) {
			throw new NullPointerException("null element");
		}
		this.element = element;
	}

	public IRodinElement getElement() {
		return element;
	}

	public IRodinFile getRodinFile() {
		if (element instanceof IRodinFile) {
			return (IRodinFile) element;
		}
		if (element instanceof IInternalElement) {
			return ((IInternalElement) element).getRodinFile();
		}
		return null;
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj.getClass() != this.getClass())
			return false;
		if (!(obj instanceof RodinLocation))
			return false;
		final RodinLocation other = (RodinLocation) obj;
		return element.equals(other.element);
	}

	@Override
	public String toString() {
		return element.toString();
	}

}
