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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.rodinp.core.IRodinFile;

/**
 * @author Nicolas Beauger
 *
 */
public class FileQueue {
	
	private final BlockingQueue<IRodinFile> queue;
	
	private final CountUpDownLatch latch = new CountUpDownLatch(0);

	public FileQueue() {
		this.queue = new LinkedBlockingQueue<IRodinFile>();
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
	public void put(IRodinFile file) throws InterruptedException {
		if (!queue.contains(file)) {
			if (RodinDBChangeListener.DEBUG) {
				System.out.println("Indexer: Enqueuing file " + file.getPath());
			}
			latch.countUp();
			queue.put(file);
		}
	}
	
	public IRodinFile take() throws InterruptedException {
		return queue.take();
	}
	
	public void fileProcessed() {
		latch.countDown();
	}
	
	public void awaitEmptyQueue() throws InterruptedException {
		latch.await();
	}
}
