/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.intelmac.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/*
 * Text with underline and strike through
 * 
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.1
 */

public class HyperlinkStyledTextTest {
	
	static StyledText text;
	static Color RED;
	static Point [] points;
	static Point currentLink;
	
		
	public static void main(String[] args) {
		Display display = new Display();
		RED = display.getSystemColor(SWT.COLOR_RED);
		Shell shell = new Shell(display);
		shell.setText("StyledText with hyperlinks");
		shell.setLayout(new FillLayout());
		text = new StyledText(shell, SWT.BORDER);
		text.setText("Link here\n and also here");
		text.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event e) {
				mouseDown(new Point(e.x, e.y));
			}
			
		});
		
		setHyperlink(new Point[] { new Point(5, 9), new Point(20, 24) });

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void mouseDown(Point location) {
//		disposeMenu();
		Point link = getLink(location);

		if (link != null) {
			setCurrentLink(link);
			activateHyperlink(link, location);
		}
		return;
	}

	private static void activateHyperlink(Point link, Point location) {
		System.out.println("Activate Hyperlink at range: " + link
				+ " at mouse location " + location);
	}

	public static void disableCurrentLink() {
		if (currentLink == null)
			return;
		StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = false;
		text.setStyleRange(style);
		currentLink = null;
	}

	public static void enableCurrentLink() {
		if (currentLink == null)
			return;
		
		StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = true;
		text.setStyleRange(style);
	}

	public static void setCurrentLink(Point link) {
		disableCurrentLink();
		currentLink = link;
		enableCurrentLink();
	}

	public static Point getLink(Point location) {
		int offset = getCharacterOffset(location);
		if (offset == -1)
			return null;
		for (Point index : points) {
			if (index.x <= offset && offset < index.y)
				return index;
		}
		return null;
	}

	static int getCharacterOffset(Point pt) {
		if (text.isDisposed())
			return -1;
		int offset;
		try {
			offset = text.getOffsetAtLocation(pt);
		}
		catch (IllegalArgumentException e) {
			return -1;
		}
		Point location = text.getLocationAtOffset(offset);

		// From the caret offset to the character offset.
		if (pt.x < location.x)
			offset = offset - 1;
		return offset;
	}

	private static void setHyperlink(Point[] links) {
		points = links;
		for (Point point : points) {
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.foreground = RED;
			text.setStyleRange(style);
		}
	}
}