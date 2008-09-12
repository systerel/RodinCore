package org.rodinp.core.index;

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

}