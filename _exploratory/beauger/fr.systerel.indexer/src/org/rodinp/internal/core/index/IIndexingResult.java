package org.rodinp.internal.core.index;

import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

public interface IIndexingResult {

	Map<IInternalElement, IDeclaration> getDeclarations();

	Set<IDeclaration> getExports();

	Map<IInternalElement, Set<IOccurrence>> getOccurrences();

	IRodinFile getFile();
	
	boolean isSuccess();

}