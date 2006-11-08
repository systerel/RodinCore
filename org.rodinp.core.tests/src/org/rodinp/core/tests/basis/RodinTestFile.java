package org.rodinp.core.tests.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.RodinFile;

public class RodinTestFile extends RodinFile {

	public static final IFileElementType ELEMENT_TYPE =
		RodinCore.getFileElementType("org.rodinp.core.tests.test");

	public RodinTestFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

}
