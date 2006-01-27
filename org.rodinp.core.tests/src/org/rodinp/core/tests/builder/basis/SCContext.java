package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.core.tests.builder.ISCContext;

public class SCContext extends RodinFile implements ISCContext {

	public SCContext(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return "org.rodinp.core.tests.scContext";
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.ISCContext#getUncheckedVersion()
	 */
	public Context getUncheckedVersion() {
		IPath path = this.getPath().removeFileExtension().addFileExtension("ctx");
		String checkedName = path.lastSegment();
		IRodinFile result = ((IRodinProject) this.getParent()).getRodinFile(checkedName);
		return (Context) result;
	}

}
