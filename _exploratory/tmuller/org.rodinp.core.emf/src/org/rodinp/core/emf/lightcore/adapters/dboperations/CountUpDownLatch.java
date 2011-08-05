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
package org.rodinp.core.emf.lightcore.adapters.dboperations;

/**
 * @author Nicolas Beauger
 * 
 */
import java.util.concurrent.locks.*;

public class CountUpDownLatch {

	/**
	 * Synchronization control For CountDownLatch. Uses AQS state to represent
	 * count.
	 */
	private static final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;

		Sync(int count) {
			setState(count);
		}

		int getCount() {
			return getState();
		}

		@Override
		public int tryAcquireShared(int acquires) {
			return getState() == 0 ? 1 : -1;
		}

		@Override
		public boolean tryReleaseShared(int releases) {
			// Decrement count; signal when transition to zero
			for (;;) {
				int c = getState();
				if (c == 0)
					return false;
				int nextc = c - releases;
				if (compareAndSetState(c, nextc))
					return nextc == 0;
			}
		}

		public void countUp() {
			int c = getState();
			setState(c + 1);
		}
	}

	private final Sync sync;

	/**
	 * Constructs a <tt>CountDownLatch</tt> initialized with the given count.
	 * 
	 * @param count
	 *            the number of times {@link #countDown} must be invoked before
	 *            threads can pass through {@link #await}.
	 * 
	 * @throws IllegalArgumentException
	 *             if <tt>count</tt> is less than zero.
	 */
	public CountUpDownLatch(int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0");
		this.sync = new Sync(count);
	}

	/**
	 * Causes the current thread to wait until the latch has counted down to
	 * zero, unless the thread is {@link Thread#interrupt interrupted}.
	 * 
	 * <p>
	 * If the current {@link #getCount count} is zero then this method returns
	 * immediately.
	 * <p>
	 * If the current {@link #getCount count} is greater than zero then the
	 * current thread becomes disabled for thread scheduling purposes and lies
	 * dormant until one of two things happen:
	 * <ul>
	 * <li>The count reaches zero due to invocations of the {@link #countDown}
	 * method; or
	 * <li>Some other thread {@link Thread#interrupt interrupts} the current
	 * thread.
	 * </ul>
	 * <p>
	 * If the current thread:
	 * <ul>
	 * <li>has its interrupted status set on entry to this method; or
	 * <li>is {@link Thread#interrupt interrupted} while waiting,
	 * </ul>
	 * then {@link InterruptedException} is thrown and the current thread's
	 * interrupted status is cleared.
	 * 
	 * @throws InterruptedException
	 *             if the current thread is interrupted while waiting.
	 */
	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}

	/**
	 * Decrements the count of the latch, releasing all waiting threads if the
	 * count reaches zero.
	 * <p>
	 * If the current {@link #getCount count} is greater than zero then it is
	 * decremented. If the new count is zero then all waiting threads are
	 * re-enabled for thread scheduling purposes.
	 * <p>
	 * If the current {@link #getCount count} equals zero then nothing happens.
	 */
	public void countDown() {
		sync.releaseShared(1);
	}

	public void countUp() {
		sync.countUp();
	}

	/**
	 * Returns the current count.
	 * <p>
	 * This method is typically used for debugging and testing purposes.
	 * 
	 * @return the current count.
	 */
	public long getCount() {
		return sync.getCount();
	}

	/**
	 * Returns a string identifying this latch, as well as its state. The state,
	 * in brackets, includes the String &quot;Count =&quot; followed by the
	 * current count.
	 * 
	 * @return a string identifying this latch, as well as its state
	 */
	@Override
	public String toString() {
		return super.toString() + "[Count = " + sync.getCount() + "]";
	}

}
