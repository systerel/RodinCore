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

import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * Class used to register controls in a StyledText.
 *  
 * @author "Thomas Muller"
 */
public class ControlHolder<U extends Control> {
	
	private final int MARGIN = 2;
	private final StyledText text;
	private final int position;
	private PaintObjectListener painter;
	
	protected final U control;
	
	public ControlHolder(StyledText text, U control, int position) {
		this.text = text;
		this.control = control;
		this.position = position;
	}
	
	public void attach() {
		addControl();
		registerPainter();
	}
	
	public void remove() {
		text.removePaintObjectListener(painter);
		if (!control.isDisposed())
			control.dispose();
	}
	
	public U getControl() {
		return control;
	}
	
	private void addControl() {
		final StyleRange style = new StyleRange();
		style.start = position;
		style.length = 1;
		control.pack();
		final Rectangle rect = control.getBounds();
		int ascent = 2 * rect.width / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.height + 2 * MARGIN);
		final Point locationAtOffset = text.getLocationAtOffset(position);
		control.setLocation(locationAtOffset);
		text.setStyleRange(style);
	}

	private void registerPainter() {
		control.setVisible(false);
		final int offset = this.position;
		final Control c = control;
		painter = new PaintObjectListener() {
			@Override
			public void paintObject(PaintObjectEvent event) {
				final StyleRange style = event.style;
				int start = style.start;
				if (start == offset) {
					final Point textSize = c.getSize();
					final int x = event.x + MARGIN;
					final int y = event.y + event.ascent - 2 * textSize.y / 3;
					c.setLocation(x, y);
					c.setVisible(true);
				}
			}
		};
		text.addPaintObjectListener(painter);
	}
	
}
