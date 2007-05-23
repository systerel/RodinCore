package org.rodinp.core.tests.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.RodinFile;

public class RodinTestFile2 extends RodinFile {

	public static final IFileElementType<RodinTestFile2> ELEMENT_TYPE =
		RodinCore.getFileElementType("org.rodinp.core.tests.test2");

	public RodinTestFile2(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<RodinTestFile2> getElementType() {
		return ELEMENT_TYPE;
	}

}
