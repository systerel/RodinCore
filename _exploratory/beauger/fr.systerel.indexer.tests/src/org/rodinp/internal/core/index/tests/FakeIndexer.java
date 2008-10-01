package org.rodinp.internal.core.index.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.RodinIndex;

public class FakeIndexer implements IIndexer {

	private static final String ID = "fr.systerel.indexer.tests.fakeindexer";

	private static final IRodinFile[] NO_FILES = new IRodinFile[0];
	
	protected final RodinIndex localIndex;

	public FakeIndexer(RodinIndex index) {
		this.localIndex = index;
	}
	
	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_FILES;
	}

	
	public void index(IRodinFile file, IIndexingToolkit index) {
		
		for (Descriptor desc: localIndex.getDescriptors()) {
			final IInternalElement element = desc.getElement();
			if (element.getRodinFile().equals(file)) {
				index.declare(element, desc.getName());
			}
			for (IOccurrence occ: desc.getOccurrences()) {
				final IRodinLocation location = occ.getLocation();
				if (isInFile(file, location)) {
					index.addOccurrence(element, occ.getKind(), location);
				}
			}
		}
	}
	
	
	private boolean isInFile(IRodinFile file, IRodinLocation location) {
		final IRodinElement locElem = location.getElement();
		final IRodinFile locElemFile;
		if (locElem instanceof IRodinFile) {
			locElemFile = (IRodinFile) locElem;
		} else if (locElem instanceof IInternalElement) {
			locElemFile = ((IInternalElement) locElem).getRodinFile();
		} else {
			return false;
		}
		return locElemFile.equals(file);
	}

	public String getId() {
		return ID;
	}

}
