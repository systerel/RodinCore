package org.eventb.core.indexer;

import static org.eventb.core.indexer.EventBIndexUtil.*;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;

public abstract class EventBIndexer implements IIndexer {

	protected static final boolean DEBUG = false;
	// FIXME manage exceptions and remove

	protected static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	protected IDeclaration indexDeclaration(IInternalElement element,
			String name, IIndexingToolkit index) throws RodinDBException {

		final IDeclaration declaration = index.declare(element, name);
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element
				.getRodinFile());

		index.addOccurrence(declaration, DECLARATION, loc);

		return declaration;
	}

	protected void indexReference(IDeclaration declaration,
			IRodinLocation location, IIndexingToolkit index) {
		index.addOccurrence(declaration, REFERENCE, location);
	}
}