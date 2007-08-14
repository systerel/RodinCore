package org.eventb.internal.ui.markers;

import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.ui.projectexplorer.TreeNode;

public interface IMarkerUIRegistry extends IMarkerRegistry {
	
	/**
	 * Check if any children of the TreeNode has a markers attached to it.
	 * 
	 * @param node
	 *            a Tree Node element, this must not be <code>null</code>.
	 * 
	 * @return Return the maximum severity of the markers found. Return -1 if no
	 *         markers found.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract int getMaxMarkerSeverity(TreeNode<?> node)
			throws CoreException;

}
