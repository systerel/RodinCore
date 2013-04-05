/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eventb.internal.ui.utils.Messages.preferencepage_autotactic_defaultprofilename;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_defaultprofilename;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

public class TacticPreferenceUtils {

	public static String getDefaultAutoTactics() {
		return preferencepage_autotactic_defaultprofilename;
	}
	
	public static String getDefaultPostTactics() {
		return preferencepage_posttactic_defaultprofilename;
	}

	public static List<String> getLabels(List<ITacticDescriptor> tactics) {
		final List<String> result = new ArrayList<String>();
		for (ITacticDescriptor tactic : tactics) {
			result.add(tactic.getTacticID());
		}
		return result;
	}

	/**
	 * Resizes the given control and packs the shell. The new width is the
	 * preferred width. The height grows if needed (preferred height), but does
	 * not decrease unless it exceeds client area.
	 * 
	 * @param control
	 */
	public static void packAll(Control control) {
		final Point size = control.getSize();
		final Rectangle clientArea = control.getParent().getClientArea();
		final Point preferredSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		preferredSize.y = Math.max(preferredSize.y, size.y);
		preferredSize.y = Math.min(preferredSize.y, clientArea.height);
		if (!preferredSize.equals(size)) {
			control.setSize(preferredSize);
			control.redraw();
		}
		
		control.getShell().pack();
	}

}
