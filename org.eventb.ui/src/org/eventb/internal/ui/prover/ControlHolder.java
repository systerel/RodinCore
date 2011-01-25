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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class used to register controls in a StyledText.
 *  
 * @author "Thomas Muller"
 */
public class ControlHolder<U extends Control> {
	
	private static final int MARGIN = 1; //px
	
	private final StyledText text;
	private int position;
	private final int length;
	private final U control;
	private PaintObjectListener painter;
	private final boolean drawBoxAround;
	
	public ControlHolder(U control, int position, boolean drawBoxAround) {
		this(control, position, 1, drawBoxAround);
	}

	public ControlHolder(U control, int position, int length,
			boolean drawBoxAround) {
		this.text = (StyledText) control.getParent();
		this.control = control;
		this.position = position;
		this.length = length;
		this.drawBoxAround = drawBoxAround;
	}
	
	public void attach() {
		addControl();
		registerPainter();
	}
	
	public void remove() {
		if (!text.isDisposed() && painter != null)
			text.removePaintObjectListener(painter);
			painter = null;
		if (!control.isDisposed())
			control.dispose();
	}
	
	public U getControl() {
		return control;
	}
	
	private void addControl() {
		final StyleRange style = new StyleRange();
		style.start = position;
		style.length = length;
		control.pack();
		final Rectangle rect = control.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		final Point loc = text.getLocationAtOffset(position);
		loc.x += MARGIN;
		loc.y += MARGIN;
		control.setLocation(loc);
		text.setStyleRange(style);
	}

	private void registerPainter() {
		final int offset = this.position;
		final Control c = this.control;
		final boolean drawBox = this.drawBoxAround;
		painter = new PaintObjectListener() {

			@Override
			public void paintObject(PaintObjectEvent event) {
				final StyleRange style = event.style;
				int start = style.start;
				if (start == offset) {
					final Point controlSize = c.getSize();
					final int x = event.x + MARGIN;
					final int height = event.ascent + event.descent;
					final int y = event.y + MARGIN + (height - controlSize.y)
							/ 2;
					c.setLocation(x, y);
					if (drawBox) {
					final Rectangle bounds = c.getBounds();
					final Color savedBgColor = event.gc.getForeground();
					event.gc.setForeground(EventBSharedColor
							.getSystemColor(SWT.COLOR_RED));
					event.gc.drawRectangle(bounds.x - 1, bounds.y - 1,
							bounds.width + 1, bounds.height + 1);
					event.gc.setForeground(savedBgColor);
					}
				}
			}
		};
		text.addPaintObjectListener(painter);
	}
	
}
