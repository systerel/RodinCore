/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted from TacticHyperlinkManager
 *******************************************************************************/
package org.eventb.internal.ui.prover;


import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class MouseHyperlinkListener implements Listener {
	
	protected final TacticHyperlinkManager manager;
	
	public MouseHyperlinkListener(TacticHyperlinkManager manager) {
		this.manager = manager;
	}
	
	// Note: problems with mouse event management prevent from using interfaces
	// MouseListener and MouseTrackListener
	
	public static class MouseDownListener extends MouseHyperlinkListener {

		public MouseDownListener(TacticHyperlinkManager manager) {
			super(manager);
		}

		@Override
		public void handleEvent(Event e) {
			manager.mouseDown(new Point(e.x, e.y));
		}

	}

	protected static class MouseMoveListener extends MouseHyperlinkListener {

		public MouseMoveListener(TacticHyperlinkManager manager) {
			super(manager);
		}

		@Override
		public void handleEvent(Event e) {
			manager.hideMenu();
			final Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	protected static class MouseEnterListener extends MouseHyperlinkListener {

		public MouseEnterListener(TacticHyperlinkManager manager) {
			super(manager);
		}
		
		@Override
		public void handleEvent(Event e) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Enter ");
			final Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	protected static class MouseHoverListener extends MouseHyperlinkListener {

		public MouseHoverListener(TacticHyperlinkManager manager) {
			super(manager);
		}

		@Override
		public void handleEvent(Event e) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Hover ");
			manager.showToolTip(new Point(e.x, e.y));
		}
	}

	protected static class MouseExitListener extends MouseHyperlinkListener {

		public MouseExitListener(TacticHyperlinkManager manager) {
			super(manager);
		}

		@Override
		public void handleEvent(Event event) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Exit ");
			manager.hideMenu();
			manager.disableCurrentLink();
		}
		
	}
	
}
