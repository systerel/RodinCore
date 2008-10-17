package org.eventb.core.indexer;

import static org.eventb.core.indexer.EventBIndexUtil.*;
import static org.rodinp.core.index.RodinIndexer.*;

import java.util.concurrent.CancellationException;

import org.eventb.core.IPredicateElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;

public abstract class EventBIndexer extends Cancellable implements IIndexer {

	private static final boolean DEBUG = false;
	// FIXME manage exceptions and remove

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	protected IIndexingToolkit index;

	// TODO change return type to boolean meaning indexing success
	public void index(IIndexingToolkit index) {
		this.index = index;
		final IInternalElement root = index.getIndexedRoot();

		try {
			index(root);
		} catch (RodinDBException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			// just return immediately
		} catch (CancellationException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			// just return immediately
		}
	}

	protected abstract void index(IInternalElement root) throws RodinDBException;

	public IRodinFile[] getDependencies(IInternalElement root) {
		try {
			return getDeps(root);
		} catch (RodinDBException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return NO_DEPENDENCIES;
		}
	}
		
	protected abstract IRodinFile[] getDeps(IInternalElement root) throws RodinDBException;
	
	protected IDeclaration indexDeclaration(IInternalElement element,
			String name) {

		final IDeclaration declaration = index.declare(element, name);
		final IRodinLocation loc = getRodinLocation(element.getRodinFile());

		index.addOccurrence(declaration, DECLARATION, loc);

		return declaration;
	}

	protected void indexReference(IDeclaration declaration,
			IRodinLocation location) {
		index.addOccurrence(declaration, REFERENCE, location);
	}

	protected void export(IDeclaration declaration) {
		index.export(declaration);
	}

	protected void checkCancel() {
		checkCancel(index);
	}

	protected void processPredicateElements(IPredicateElement[] preds,
			SymbolTable symbolTable) throws RodinDBException {
		for (IPredicateElement elem : preds) {
			final PredicateIndexer predIndexer = new PredicateIndexer(elem,
					symbolTable, index);
			predIndexer.process();

			checkCancel();
		}
	}

	/**
	 * @param file
	 */
	protected void throwIllArgException(IInternalElement root) {
		throw new IllegalArgumentException("Cannot index " + root
				+ ": bad element type");
	}
	
}