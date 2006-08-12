package org.rodinp.core.tests.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinFile;

public class RodinTestFile extends RodinFile {

	public RodinTestFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return "org.rodinp.core.tests.test";
	}


}
