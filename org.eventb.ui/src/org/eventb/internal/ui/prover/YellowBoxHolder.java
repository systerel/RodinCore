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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author "Thomas Muller"
 * 
 */
public class YellowBoxHolder extends ControlHolder {

	// The yellow boxes to insert some input
	private IEventBInputText box;

	public YellowBoxHolder(PredicateRow row, ControlMaker maker,
			int offset, boolean drawBoxAround) {
		super(row, maker, offset, drawBoxAround);
	}

	@Override
	public void remove() {
		if (box != null)
			box.dispose();
		super.remove();
	}

	@Override
	protected void paintAndPlace(PaintObjectEvent event) {
		super.paintAndPlace(event);
		if (box == null) {
			box = new EventBMath((Text) control);
			box.getTextWidget().addVerifyListener(new YellowBoxVerifyListener(this));			
		}
	}

	public String getInputString() {
		if (box != null){
			return box.getTextWidget().getText();			
		}
		return "";
	}
	
	public static class YellowBoxVerifyListener implements VerifyListener {
		
		private final YellowBoxHolder holder;
		
		public YellowBoxVerifyListener(YellowBoxHolder holder) {
			this.holder = holder;
		}

		@Override
		public void verifyText(VerifyEvent e) {
			holder.setStyleRange(false);
		}
		
	}
	
}
