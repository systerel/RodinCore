package org.rodinp.core.index;

import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public interface IIndexer {

	public void index(IRodinFile file, IIndexingFacade index);

	/**
	 * Computes and returns the dependencies of the given file.
	 * 
	 * @param file
	 * @return an array containing the file dependencies.
	 */
	public IRodinFile[] getDependencies(IRodinFile file);

	/**
	 * Computes and returns the elements exported by the given file.
	 * 
	 * @param file
	 * @return the exported elements associated to their used-defined name.
	 * FIXME accept null return ?
	 */

	// TODO remove: should be done one by one through IndexingFacade
	
	public Map<IInternalElement, String> getExports(IRodinFile file);

}