package org.rodinp.internal.core.index;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
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
		final List<IRodinFile> affectedFiles = getAffectedFiles(delta);
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
	
	private List<IRodinFile> getAffectedFiles(IRodinElementDelta delta) { // TODO remove recursion
		final IRodinElement element = delta.getElement();
		if (element instanceof IRodinFile) {
			final ArrayList<IRodinFile> result = new ArrayList<IRodinFile>();
			result.add((IRodinFile) element);
			return result;
		}
		List<IRodinFile> list = new ArrayList<IRodinFile>();
		for (IRodinElementDelta d: delta.getAffectedChildren()) {
			final List<IRodinFile> affectedFiles = getAffectedFiles(d);
			list.addAll(affectedFiles);
		}
		return list;
	}
	
}
