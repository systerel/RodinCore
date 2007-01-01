/*******************************************************************************
 * Inspired by org.eclipse.ui.internal.ide.misc.OverlayIcon which is:
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * An OverlayIcon consists of a main icon and several adornments.
 */
public class OverlayIcon extends CompositeImageDescriptor {

	static final int DEFAULT_WIDTH = 20;

	static final int DEFAULT_HEIGHT = 16;

	private Point fSize = null;

	private ImageDescriptor fBase;

	private Collection<ImageDescriptor> topRightOverlays;

	private Collection<ImageDescriptor> topLeftOverlays;

	private Collection<ImageDescriptor> bottomRightOverlays;

	private Collection<ImageDescriptor> bottomLeftOverlays;

	public OverlayIcon(ImageDescriptor base) {
		fBase = base;
		topRightOverlays = new ArrayList<ImageDescriptor>();
		topLeftOverlays = new ArrayList<ImageDescriptor>();
		bottomRightOverlays = new ArrayList<ImageDescriptor>();
		bottomLeftOverlays = new ArrayList<ImageDescriptor>();
		fSize = new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public void addTopRight(ImageDescriptor imageDescriptor) {
		topRightOverlays.add(imageDescriptor);
	}

	public void addTopLeft(ImageDescriptor imageDescriptor) {
		topLeftOverlays.add(imageDescriptor);
	}

	public void addBottomRight(ImageDescriptor imageDescriptor) {
		bottomRightOverlays.add(imageDescriptor);
	}

	public void addBottomLeft(ImageDescriptor imageDescriptor) {
		bottomLeftOverlays.add(imageDescriptor);
	}

	/**
	 * @see CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	@Override
	protected void drawCompositeImage(int width, int height) {
		ImageData bg;
		if (fBase == null || (bg = fBase.getImageData()) == null)
			bg = DEFAULT_IMAGE_DATA;
		drawImage(bg, 0, 0);

		drawTopRight();
		drawTopLeft();
		drawBottomRight();
		drawBottomLeft();
	}

	/**
	 * @see CompositeImageDescriptor#getSize()
	 */
	@Override
	protected Point getSize() {
		return fSize;
	}

	protected void drawTopRight() {
		int x = getSize().x;

		for (ImageDescriptor overlay : topRightOverlays) {
			if (overlay != null) {
				ImageData id = overlay.getImageData();
				x -= id.width;
				drawImage(id, x, 0);
			}
		}
	}

	protected void drawTopLeft() {
		int x = 0;
		for (ImageDescriptor overlay : topLeftOverlays) {
			if (overlay != null) {
				ImageData id = overlay.getImageData();
				drawImage(id, x, 0);
				x += id.width;
			}
		}
	}

	protected void drawBottomRight() {
		Point size = getSize();
		int x = size.x;
		for (ImageDescriptor overlay : bottomRightOverlays) {
			if (overlay != null) {
				ImageData id = overlay.getImageData();
				x -= id.width;
				drawImage(id, x, size.y - id.height);
			}
		}
	}

	protected void drawBottomLeft() {
		int x = 0;
		for (ImageDescriptor overlay : bottomLeftOverlays) {
			if (overlay != null) {
				ImageData id = overlay.getImageData();
				drawImage(id, x, getSize().y - id.height);
				x += id.width;
			}
		}
	}

}