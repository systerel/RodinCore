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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * The manager responsible to repaint the controls added to the styled text
 * component.
 * 
 * @author "Thomas Muller"
 * 
 */
public class ControlPainter implements PaintObjectListener {

	private static final Color RED = EventBSharedColor
			.getSystemColor(SWT.COLOR_RED);
	private final Map<Integer, ControlHolder> holders;
	private boolean painting = false;

	public ControlPainter() {
		this.holders = new HashMap<Integer, ControlHolder>();
	}

	@Override
	public void paintObject(PaintObjectEvent event) {
		if (painting)
			return;
		try {
			painting = true;
			final StyleRange style = event.style;
			final ControlHolder holder = holders.get(style.start);
			if (holder != null) {
				holder.paintAndPlace(event);
				drawBoxAround(event.gc, holder);
			}
		} finally {
			painting = false;
		}
	}

	private void drawBoxAround(GC gc, final ControlHolder holder) {
		if (!holder.drawBoxAround)
			return;
		final Rectangle b = holder.control.getBounds();
		final Color savedBgColor = gc.getForeground();
		gc.setForeground(RED);
		gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1, b.height + 1);
		gc.setForeground(savedBgColor);
	}
	
	public void registerControlHolder(ControlHolder holder) {
		holders.put(holder.offset, holder);
	}
	
	public void clear() {
		holders.clear();
	}

}
