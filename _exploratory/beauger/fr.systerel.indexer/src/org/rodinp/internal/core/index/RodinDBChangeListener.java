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
	}

	public RodinDBChangeListener(BlockingQueue<IRodinFile> queue) {
		this.queue = queue;
	}
	
	// Concurrency: no synchronization is required in the {contains;put} block
	// The possible situations are the following:
	// 1/ queue.contains(file) is true, then queue.take is called and takes file
	// => the condition becomes false but put is not called.
	// This is not a problem since the taker will process an up to date version 
	// of the file (after the current delta event that caused a possible put)
	//  
	// 2/ queue.contains(file) is false, then queue.take is called before put
	// but it cannot take the file to put since it is not yet present !
	private void processDelta(IRodinElementDelta delta) {
		// TODO also listen to project creation and deletion
		// TODO what about project open and close initiated by user: use
		// workspace listener?
		final List<IRodinFile> affectedFiles = new ArrayList<IRodinFile>();
		addAffectedFiles(delta, affectedFiles);
		boolean interrupted = false;
		try {
			while(true) {
				try {
					for (IRodinFile file : affectedFiles) {
						if (!queue.contains(file)) {
							queue.put(file);
						}
					}
					return;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
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
		for (IRodinElementDelta childDelta : delta.getAffectedChildren()) {
			addAffectedFiles(childDelta, accumulator);
		}
	}

}
