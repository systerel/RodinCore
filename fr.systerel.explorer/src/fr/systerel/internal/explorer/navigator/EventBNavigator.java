/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class EventBNavigator extends CommonNavigator {

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		final CommonViewer viewer = super.createCommonViewer(aParent);
		NavigatorController.setUpNavigator(viewer);
		return viewer;
	}

}
