package org.eventb.internal.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;

public class MarkerUIRegistry implements IMarkerUIRegistry {

	private static IMarkerUIRegistry instance;
	
	private MarkerUIRegistry() {
		// Singleton: Private constructor.
	}
	
	public static IMarkerUIRegistry getDefault() {
		if (instance == null) {
			instance = new MarkerUIRegistry();
		}
		return instance;
	}
	
	public int getMaxMarkerSeverity(TreeNode<?> node)
			throws CoreException {
		assert node != null;
		IRodinElement[] elements = node.getChildren();
		int severity = -1;
		for (IRodinElement element : elements) {
			int newSeverity = MarkerRegistry.getDefault().getMaxMarkerSeverity(
					element);
			if (severity < newSeverity)
				severity = newSeverity;
		}
		return severity;
	}

	public IMarker[] getMarkers(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMarkers(element);
	}

	public int getMaxMarkerSeverity(IRodinElement element) throws CoreException {
		return MarkerRegistry.getDefault().getMaxMarkerSeverity(element);
	}

}
