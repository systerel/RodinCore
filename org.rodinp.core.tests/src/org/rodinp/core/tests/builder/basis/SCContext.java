package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.tests.builder.ISCContext;

public class SCContext extends Component implements ISCContext {

	public SCContext(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<ISCContext> getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.ISCContext#getUncheckedVersion()
	 */
	public Context getUncheckedVersion() {
		return (Context) getAlternateVersion("ctx");
	}

}
