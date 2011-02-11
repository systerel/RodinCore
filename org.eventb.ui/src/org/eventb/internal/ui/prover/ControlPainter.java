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

import java.util.ArrayList;
import java.util.List;

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
	private final List<ControlHolder> holders;
	private boolean painting = false;

	public ControlPainter() {
		this.holders = new ArrayList<ControlHolder>();
	}

	@Override
	public void paintObject(PaintObjectEvent event) {
		if (painting)
			return;
		try {
			painting = true;
			final StyleRange style = event.style;
			int start = style.start;
			for (ControlHolder holder : holders) {
				if (start != holder.offset) {
					continue;
				}
				if (holder.getLastPaintTime() == event.time) {
					continue;
				}
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
		final Rectangle bounds = holder.control.getBounds();
		final Color savedBgColor = gc.getForeground();
		gc.setForeground(RED);
		gc.drawRectangle(bounds.x - 1, bounds.y - 1, bounds.width + 1,
				bounds.height + 1);
		gc.setForeground(savedBgColor);
	}
	
	public void registerControlHolder(ControlHolder holder) {
		if (holders.contains(holder)) {
			return;
		}
		holders.add(holder);
	}
	
	public void clear() {
		holders.clear();
	}

}
