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

public abstract class EventBIndexer implements IIndexer {

	private static final boolean DEBUG = false;
	// FIXME manage exceptions and remove

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	protected IIndexingToolkit index;

	// TODO change return type to boolean meaning indexing success
	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		try {
			index(file);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			if (DEBUG) {
				e.printStackTrace();
			}
		} catch (CancellationException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			// just return immediately
		}
	}

	protected abstract void index(IRodinFile file) throws RodinDBException;

	public IRodinFile[] getDependencies(IRodinFile file) {
		try {
			return getDeps(file);
		} catch (RodinDBException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return NO_DEPENDENCIES;
		}
	}
		
	protected abstract IRodinFile[] getDeps(IRodinFile file) throws RodinDBException;
	
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

	/**
	 * 
	 */
	protected void checkCancel() {
		if (index.isCancelled()) {
			throw new CancellationException(getClass().getSimpleName()
					+ ": cancelled !");
		}
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
	protected void throwIllArgException(IRodinFile file) {
		throw new IllegalArgumentException("Cannot index " + file
				+ ": bad file type");
	}

}