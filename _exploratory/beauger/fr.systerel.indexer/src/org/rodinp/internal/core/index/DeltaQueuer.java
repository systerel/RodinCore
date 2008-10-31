/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;

public class DeltaQueuer implements IElementChangedListener {
	
	public static boolean DEBUG = false;

	private final FileQueue queue;

	private final ThreadLocal<Boolean> interrupted = new  ThreadLocal<Boolean>();
	
		
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		interrupted.set(false);
		try {
			while (true) {
				try {
					processDelta(delta);
					return;
				} catch (InterruptedException e) {
					interrupted.set(true);
				}
			}
		} finally {
			if (interrupted.get()) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public DeltaQueuer(FileQueue queue) {
		this.queue = queue;
	}
	
	private void processDelta(IRodinElementDelta delta) throws InterruptedException {
		// TODO also listen to project creation and deletion
		// TODO what about project open and close initiated by user: use
		// workspace listener?
		enqueueAffectedFiles(delta);
	}


	private void enqueueIfNotPresent(IRodinFile file)
			throws InterruptedException {
			queue.put(file);
	}
	
	private void enqueueAffectedFiles(IRodinElementDelta delta) throws InterruptedException {
		final IRodinElement element = delta.getElement();
		if (!(element instanceof IOpenable)) {
			// No chance to find a file below
			return;
		}
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (DEBUG) {
				System.out.println("Indexer: File " + file.getPath() + " has changed.");
			}
			enqueueIfNotPresent(file);
			return;
		}
		for (IRodinElementDelta childDelta : delta.getAffectedChildren()) {
			enqueueAffectedFiles(childDelta);
		}
	}

}
