/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeltaQueue {

	private final BlockingQueue<IIndexDelta> queue;

	private final CountUpDownLatch latch = new CountUpDownLatch(0);

	public DeltaQueue() {
		this.queue = new LinkedBlockingQueue<IIndexDelta>();
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
	public void put(IIndexDelta delta, boolean allowDuplicate)
			throws InterruptedException {
		if (allowDuplicate || !queue.contains(delta)) {
			if (DeltaQueuer.DEBUG) {
				System.out.println("Indexer: Enqueuing delta "
						+ delta.getElement().getPath()
						+ " reason: "
						+ delta.getKind());
			}
			latch.countUp();
			queue.put(delta);
		}
	}

	public IIndexDelta take() throws InterruptedException {
		return queue.take();
	}

	public void drainTo(Collection<? super IIndexDelta> c) {
		queue.drainTo(c);
	}

	public void deltaProcessed() {
		latch.countDown();
	}

	public void awaitEmptyQueue() throws InterruptedException {
		latch.await();
	}
	
	public boolean isProcessed() {
		return latch.getCount() == 0;
	}
	
	public void putAll(Collection<? extends IIndexDelta> c)
			throws InterruptedException {
		for (IIndexDelta delta : c) {
			put(delta, false);
		}
	}
}
