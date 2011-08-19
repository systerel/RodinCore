/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover.tests;
 
/*
 * Menu example snippet: create a popup menu (set in multiple controls)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TestMenu {

	static Listener enterListener;
	static Listener exitListener;
	static Listener downListener;
	static Listener hoverListener;
	static Listener moveListener;
	
public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	final StyledText text = new StyledText(shell, SWT.BORDER | SWT.MULTI);
	text.setText("Hello world!");
	text.setBounds(0, 0, 200, 100);
	text.setEditable(false);
	final HyperlinkManager manager = new HyperlinkManager(text) {

		@Override
		void disableListeners() {
			text.removeListener(SWT.MouseEnter, enterListener);
			text.removeListener(SWT.MouseMove, moveListener);
			text.removeListener(SWT.MouseExit, exitListener);
			text.removeListener(SWT.MouseHover, hoverListener);
			text.removeListener(SWT.MouseDown, downListener);
		}

		@Override
		void enableListeners() {
			text.addListener(SWT.MouseEnter, enterListener);
			text.addListener(SWT.MouseMove, moveListener);
			text.addListener(SWT.MouseExit, exitListener);
			text.addListener(SWT.MouseHover, hoverListener);
			text.addListener(SWT.MouseDown, downListener);
		}
		
	};
	enterListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
//			System.out.println("Mouse Enter");
			Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	};
	text.addListener(SWT.MouseEnter, enterListener);
	
	exitListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
//			System.out.println("Mouse Exit");
			manager.disposeMenu();
			manager.disableCurrentLink();
		}
	};
	text.addListener(SWT.MouseExit, exitListener);
	
	moveListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
//			System.out.println("Mouse Move");
			manager.disposeMenu();
			Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}	
	};
	text.addListener(SWT.Move, moveListener);
	
	downListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
//			System.out.println("Mouse Down");
			manager.mouseDown(new Point(e.x, e.y));
		}	
	};
	text.addListener(SWT.DOWN, downListener);
	
	hoverListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
//			System.out.println("Mouse Hover");
			manager.showToolTip(new Point(e.x, e.y));
		}		
	};
	text.addListener(SWT.MouseHover, hoverListener);
	
	shell.setSize (300, 300);
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
