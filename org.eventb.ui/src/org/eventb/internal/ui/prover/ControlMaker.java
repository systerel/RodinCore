/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Control creation class. 
 * 
 * @author "Thomas Muller"
 */
public abstract class ControlMaker {
	
	private final Composite parent;
	private Rectangle bounds;
	private boolean painting = false;
	
	public ControlMaker(Composite parent) {
		this.parent = parent;
	}
	
	public Control getControl(){
		final Control c = makeControl();
		c.pack();
		return c;
	}
	
	public abstract Control makeControl();
	
	public Rectangle getBounds(ControlHolder holder){
		if (bounds == null) {
			final Control c = makeControl();
			c.pack();
			bounds = c.getBounds();
			holder.setControl(c);
			c.setVisible(false);
		}
		return bounds;
	}
	
	public Composite getParent() {
		return parent;
	}

	public boolean isPainting() {
		return painting;
	}
	
	public void setPainting(boolean painting){
		this.painting = painting;
	}
	
}