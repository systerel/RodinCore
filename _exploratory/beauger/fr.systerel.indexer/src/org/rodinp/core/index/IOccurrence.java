package org.rodinp.core.index;

import org.rodinp.core.IRodinFile;


public interface IOccurrence {

	IOccurrenceKind getKind();

	IRodinLocation getLocation();

	IRodinFile getRodinFile();

}