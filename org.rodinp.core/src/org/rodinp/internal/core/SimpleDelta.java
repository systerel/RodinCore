/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.SimpleDelta
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinElementDelta;

/**
 * A simple Rodin element delta that remembers the kind of changes only.
 */
public class SimpleDelta {

	/*
	 * @see IRodinElementDelta#getKind()
	 */
	protected int kind = 0;
	
	/*
	 * @see IRodinElementDelta#getFlags()
	 */
	protected int changeFlags = 0;
	
	/*
	 * Marks this delta as added
	 */
	public void added() {
		this.kind = IRodinElementDelta.ADDED;
	}
	
	/*
	 * Marks this delta as changed with the given change flag
	 */
	public void changed(int flags) {
		this.kind = IRodinElementDelta.CHANGED;
		this.changeFlags |= flags;
	}
	
	/*
	 * @see IRodinElementDelta#getFlags()
	 */
	public int getFlags() {
		return this.changeFlags;
	}
	
	/*
	 * @see IRodinElementDelta#getKind()
	 */
	public int getKind() {
		return this.kind;
	}

	/*
	 * Marks this delta as removed
	 */
	public void removed() {
		this.kind = IRodinElementDelta.REMOVED;
		this.changeFlags = 0;
	}
	
	protected void toDebugString(StringBuffer buffer) {
		buffer.append("["); //$NON-NLS-1$
		switch (getKind()) {
			case IRodinElementDelta.ADDED :
				buffer.append('+');
				break;
			case IRodinElementDelta.REMOVED :
				buffer.append('-');
				break;
			case IRodinElementDelta.CHANGED :
				buffer.append('*');
				break;
			default :
				buffer.append('?');
				break;
		}
		buffer.append("]: {"); //$NON-NLS-1$
		toDebugString(buffer, getFlags());
		buffer.append("}"); //$NON-NLS-1$
	}

	protected boolean toDebugString(StringBuffer buffer, int flags) {
		return false;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toDebugString(buffer);
		return buffer.toString();
	}
}
