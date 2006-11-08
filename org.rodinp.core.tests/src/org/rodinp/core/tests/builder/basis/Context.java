package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.tests.builder.IContext;

public class Context extends Component implements IContext {

	public Context(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.IContext#getCheckedVersion()
	 */
	public SCContext getCheckedVersion() {
		return (SCContext) getAlternateVersion("csc");
	}
	
}
