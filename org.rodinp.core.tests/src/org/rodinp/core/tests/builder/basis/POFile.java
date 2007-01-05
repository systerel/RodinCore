package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.tests.builder.IPOFile;
import org.rodinp.core.tests.builder.ISCContext;
import org.rodinp.core.tests.builder.ISCMachine;

public class POFile extends Component implements IPOFile {

	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<IPOFile> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCContext getCheckedContext() {
		return (ISCContext) getAlternateVersion("csc");
	}

	public ISCMachine getCheckedMachine() {
		return (ISCMachine) getAlternateVersion("msc") ;
	}

}
