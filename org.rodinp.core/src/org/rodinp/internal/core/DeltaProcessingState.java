/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.DeltaProcessingState
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinProject;

/**
 * Keep the global states used during Rodin element delta processing.
 */
public class DeltaProcessingState implements IResourceChangeListener {
	
	/*
	 * Collection of listeners for Rodin element deltas
	 */
	public IElementChangedListener[] elementChangedListeners = new IElementChangedListener[5];
	public int[] elementChangedListenerMasks = new int[5];
	public int elementChangedListenerCount = 0;
	
	/*
	 * The delta processor for the current thread.
	 */
	private ThreadLocal<DeltaProcessor> deltaProcessors = new ThreadLocal<DeltaProcessor>();
	
	/**
	 * This is a cache of the projects before any project addition/deletion has started.
	 */
	// TODO move dbProjectsCache in DeltaProcessor, so that it's ThreadLocal!
	public IRodinProject[] dbProjectsCache;
	
	/*
	 * Need to clone defensively the listener information, in case some listener is reacting to some notification iteration by adding/changing/removing
	 * any of the other (for example, if it deregisters itself).
	 */
	public void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		for (int i = 0; i < this.elementChangedListenerCount; i++){
			if (this.elementChangedListeners[i].equals(listener)){
				
				// only clone the masks, since we could be in the middle of notifications and one listener decide to change
				// any event mask of another listeners (yet not notified).
				int cloneLength = this.elementChangedListenerMasks.length;
				System.arraycopy(this.elementChangedListenerMasks, 0, this.elementChangedListenerMasks = new int[cloneLength], 0, cloneLength);
				this.elementChangedListenerMasks[i] = eventMask; // could be different
				return;
			}
		}
		// may need to grow, no need to clone, since iterators will have cached original arrays and max boundary and we only add to the end.
		int length;
		if ((length = this.elementChangedListeners.length) == this.elementChangedListenerCount){
			System.arraycopy(this.elementChangedListeners, 0, this.elementChangedListeners = new IElementChangedListener[length*2], 0, length);
			System.arraycopy(this.elementChangedListenerMasks, 0, this.elementChangedListenerMasks = new int[length*2], 0, length);
		}
		this.elementChangedListeners[this.elementChangedListenerCount] = listener;
		this.elementChangedListenerMasks[this.elementChangedListenerCount] = eventMask;
		this.elementChangedListenerCount++;
	}

	public DeltaProcessor getDeltaProcessor() {
		DeltaProcessor deltaProcessor = this.deltaProcessors.get();
		if (deltaProcessor != null) return deltaProcessor;
		deltaProcessor = new DeltaProcessor(this, RodinDBManager.getRodinDBManager());
		this.deltaProcessors.set(deltaProcessor);
		return deltaProcessor;
	}

	public void removeElementChangedListener(IElementChangedListener listener) {
		
		for (int i = 0; i < this.elementChangedListenerCount; i++){
			
			if (this.elementChangedListeners[i].equals(listener)){
				
				// need to clone defensively since we might be in the middle of listener notifications (#fire)
				int length = this.elementChangedListeners.length;
				IElementChangedListener[] newListeners = new IElementChangedListener[length];
				System.arraycopy(this.elementChangedListeners, 0, newListeners, 0, i);
				int[] newMasks = new int[length];
				System.arraycopy(this.elementChangedListenerMasks, 0, newMasks, 0, i);
				
				// copy trailing listeners
				int trailingLength = this.elementChangedListenerCount - i - 1;
				if (trailingLength > 0){
					System.arraycopy(this.elementChangedListeners, i+1, newListeners, i, trailingLength);
					System.arraycopy(this.elementChangedListenerMasks, i+1, newMasks, i, trailingLength);
				}
				
				// update manager listener state (#fire need to iterate over original listeners through a local variable to hold onto
				// the original ones)
				this.elementChangedListeners = newListeners;
				this.elementChangedListenerMasks = newMasks;
				this.elementChangedListenerCount--;
				return;
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		boolean isPostChange = event.getType() == IResourceChangeEvent.POST_CHANGE;
		try {
			getDeltaProcessor().resourceChanged(event);
		} finally {
			// TODO (jerome) see 47631, may want to get rid of following so as to reuse delta processor ? 
			if (isPostChange) {
				this.deltaProcessors.set(null);
			}
		}

	}

}
