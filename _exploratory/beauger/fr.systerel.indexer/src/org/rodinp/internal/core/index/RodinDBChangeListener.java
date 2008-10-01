package org.rodinp.internal.core.index;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;

public class RodinDBChangeListener implements IElementChangedListener {



	private final BlockingQueue<IRodinFile> queue;

	public synchronized void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
//		System.out.println(event);
	}

	public RodinDBChangeListener(BlockingQueue<IRodinFile> queue) {
		this.queue = queue;
	}

	private void processDelta(IRodinElementDelta delta) {
		// TODO also listen to project creation and deletion
		// TODO what about project open and close initiated by user: use workspace listener?
		final List<IRodinFile> affectedFiles = new ArrayList<IRodinFile>();
		addAffectedFiles(delta, affectedFiles);
		for (IRodinFile file : affectedFiles) {
			if (!queue.contains(file)) {
				try {
					queue.put(file);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void addAffectedFiles(IRodinElementDelta delta,
			List<IRodinFile> accumulator) {
		final IRodinElement element = delta.getElement();
		if (!(element instanceof IOpenable)) {
			// No chance to find a file below
			return;
		}
		if (element instanceof IRodinFile) {
			accumulator.add((IRodinFile) element);
			return;
		}
		for (IRodinElementDelta childDelta: delta.getAffectedChildren()) {
			addAffectedFiles(childDelta, accumulator);
		}
	}
	
}
