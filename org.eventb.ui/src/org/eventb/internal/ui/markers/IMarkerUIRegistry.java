package org.eventb.internal.ui.markers;

import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IElementType;
import org.rodinp.core.IParent;

public interface IMarkerUIRegistry extends IMarkerRegistry {
	
	/**
	 * Get the maximum marker severity for children of the input tree node.
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

	/**
	 * Get the maximum marker severity for children of the input type of the
	 * input parent.
	 * 
	 * @param parent
	 *            a Tree Node element, this must not be <code>null</code>.
	 * @param childType type of the children
	 * 
	 * @return Return the maximum severity of the markers found. Return -1 if no
	 *         markers found.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract int getMaxMarkerSeverity(IParent parent,
			IElementType<?> childType) throws CoreException;

}
