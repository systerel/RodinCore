package org.rodinp.core.index;

import org.rodinp.core.IRodinFile;

public interface IIndexer {

	public boolean canIndex(IRodinFile file);

	public void index(IRodinFile file, IRodinIndex index);

}