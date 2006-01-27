package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinFile;

public class POFile extends RodinFile {

	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return "org.rodinp.core.tests.poFile";
	}


}
