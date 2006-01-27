package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.core.tests.builder.IContext;
import org.rodinp.core.tests.builder.IDependency;

public class Context extends RodinFile implements IContext {

	public Context(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return "org.rodinp.core.tests.context";
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.IContext#getCheckedVersion()
	 */
	public SCContext getCheckedVersion() {
		IPath path = this.getPath().removeFileExtension().addFileExtension("csc");
		String checkedName = path.lastSegment();
		IRodinFile result = ((IRodinProject) this.getParent()).getRodinFile(checkedName);
		return (SCContext) result;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.IContext#getUsedContexts()
	 */
	public IContext[] getUsedContexts() throws RodinDBException {
		IRodinElement[] deps;
		deps = this.getChildrenOfType(IDependency.ELEMENT_TYPE);
		IContext[] result = new IContext[deps.length];
		for (int i = 0; i < deps.length; i++) {
			String depName = deps[i].getElementName();
			result[i] = (IContext) getRodinProject().getRodinFile(depName + ".ctx");
		}
		return result;
	}
	
}
