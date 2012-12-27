/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * @author htson
 *         <p>
 *         This class implements the decorator for Text input that is used in
 *         the Editable Tree Viewer.
 */
public abstract class TimerStyledText implements ModifyListener {

	// The time that the text is last modified.
	int lastModify;
	
	private int delay;
	
	protected StyledText text;

	/**
	 * @author htson
	 *         <p>
	 *         This class implements the auto commit behaviour of the text.
	 */
	private class TimeRunnable implements Runnable {
		private int time;

		TimeRunnable(int time) {
			this.time = time;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (lastModify == time) {
				if (!text.isDisposed())
					response();
			}
		}

	}

	protected abstract void response();

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param text
	 *            The actual Text Widget.
	 * @param delay
	 *            the time for delaying in milli-seconds
	 */
	public TimerStyledText(StyledText text, int delay) {
		this.text = text;
		this.delay = delay;
		text.addModifyListener(this);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		if (text.getEditable()) {
			lastModify = e.time;
			text.getDisplay().timerExec(delay, new TimeRunnable(e.time));
		}
	}

}
