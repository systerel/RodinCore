package org.rodinp.core.index;

import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public interface IIndexer {

	/**
	 * True iff the indexer can index the given file and compute its
	 * dependencies.
	 * 
	 * @param file
	 * @return a boolean indicating its capabilities at indexing the given file.
	 */
	public boolean canIndex(IRodinFile file);

	public void index(IRodinFile file, IndexingFacade index);

	/**
	 * Computes and returns the dependencies of the given file.
	 * 
	 * @param file
	 * @return a non null array containing the file dependencies.
	 */
	public IRodinFile[] getDependencies(IRodinFile file);

	/**
	 * Computes and returns the elements exported by the given file.
	 * 
	 * @param file
	 * @return the exported elements associated to their used-defined name.
	 * FIXME accept null return ?
	 */
	public Map<IInternalElement, String> getExports(IRodinFile file);

}