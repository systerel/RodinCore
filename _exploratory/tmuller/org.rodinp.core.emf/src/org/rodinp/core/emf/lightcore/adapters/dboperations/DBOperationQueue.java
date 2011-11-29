/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters.dboperations;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Nicolas Beauger
 * 
 * NOTE : imported from org.rodinp.internal.core.indexer.DeltaQueue;
 *        Simplified to the simple case of queue and dequeue.
 */
public class DBOperationQueue {

	private final BlockingQueue<ElementOperation> queue;

	public DBOperationQueue() {
		this.queue = new LinkedBlockingQueue<ElementOperation>();
	}

	public void put(ElementOperation operation)
			throws InterruptedException {
			if (DeltaProcessManager.DEBUG) {
				System.out.println("DBOperationQueue: Enqueuing operation "
						+ operation.getElement().getPath() + " type: "
						+ operation.getType());
			}
			queue.put(operation);
	}

	public ElementOperation take() throws InterruptedException {
		return queue.take();
	}

	public void putAll(Collection<? extends ElementOperation> c)
			throws InterruptedException {
		for (ElementOperation op : c) {
			put(op);
		}
	}
	
}
